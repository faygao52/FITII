package com.fsl.mcu.kinetislist;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import android.content.Context;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class DeviceList implements Runnable {
	private static final String TAG = "DeviceList implements Runnable";
	private static boolean  initialized = false;
	public static ArrayList<DBdefine.HeaderItem> headerList = null;
	public static ArrayList<DBdefine.DeviceRow> deviceList = null;
	public static ArrayList<DBdefine.ColumnValue> valueList = null;

	private static DatabaseManager dbManager;
	private static waitingRoom nurse;
	//private static int sourceID;
	private static class Task {
		int		msg;
		boolean block;	// if this request is blocked.
		Messenger replyto;
		Object objArg1;
		Object objArg2;
		Object objArg3;
	}
	private static ArrayBlockingQueue<Task> taskQueue;
	
	public DeviceList(Context context)  {
		if (initialized == false) {
			dbManager = new DatabaseManager(context);		
			taskQueue = new ArrayBlockingQueue<Task>(4);
			
			headerList = new ArrayList<DBdefine.HeaderItem>();
			deviceList = new ArrayList<DBdefine.DeviceRow>();
			
			dbManager.initDBM();
			initialized = true;
		}
		
		nurse = new waitingRoom();
	}

	public void getContents() {
		dbManager.getListData("*", headerList, deviceList);
	}
	
	@Override
	public void run()  {
		Task oneTask;
		try {
			while ((oneTask = taskQueue.take()) != null) {
		//		Thread t = Thread.currentThread();
		//		Log.i(TAG, "Loop Thread: "+ t.getName() + ", ID = " + t.getId() + " msg = " + oneTask.msg);
				switch (oneTask.msg) {
				case DBdefine.MSG.MSG_TASK_LIST_SOURCES:
					ArrayList<String> sourceList =(ArrayList<String>) oneTask.objArg1;
					ArrayList<Integer> sourcntList = (ArrayList<Integer>) oneTask.objArg2;
					dbManager.getSources(sourceList,sourcntList);
					nurse.set();
					break;
				case DBdefine.MSG.MSG_TASK_LIST_FAMILIES:
					ArrayList<String> familyList = (ArrayList<String>) oneTask.objArg1;
					ArrayList<Integer> cntList = (ArrayList<Integer>) oneTask.objArg2;
					String source = (String)oneTask.objArg3;
					dbManager.getFamilies(source, familyList, cntList);
			//		if (oneTask.block)
						nurse.set();
			//		else
			//			oneTask.replyto.send(Message.obtain(null, DBdefine.MSG.MSG_TASK_LIST_FAMILIES, 0, 0, familyList));
					break;
				case DBdefine.MSG.MSG_TASK_LIST_CONTENTS:
					dbManager.getListData((String)oneTask.objArg1, headerList, deviceList);
					oneTask.replyto.send(Message.obtain(null, DBdefine.MSG.MSG_TASK_LIST_CONTENTS, 0, 0, null));
					break;
				case DBdefine.MSG.MSG_TASK_LIST_HEADERS:
					dbManager.readHeaderList((ArrayList<DBdefine.HeaderItem>)oneTask.objArg1, true);
					nurse.set();
					break;
				case DBdefine.MSG.MSG_TASK_LIST_COLUMN_VALUES:
					valueList = dbManager.getColumnValues((String)oneTask.objArg1, (String)oneTask.objArg2);
					//valueList = dbManager.getColumnValues(headerList, deviceList, (String)oneTask.objArg2); 
					nurse.set();
					break;
				case DBdefine.MSG.MSG_TASK_CHANGE_COLUMN_VALUES:
					dbManager.setColumnValues(valueList, (String)oneTask.objArg1);
					nurse.set();
					break;
				case DBdefine.MSG.MSG_TASK_LIST_COLUMN_FILTERS:
					dbManager.getColumnFilters((ArrayList<DBdefine.ColumnFilter>)oneTask.objArg1);
					Log.d(TAG,"MSG Filter:");
					nurse.set();
					break;
				case DBdefine.MSG.MSG_TASK_CLEAR_COLUMN_FILTERS:
					Log.d(TAG,"clear column filters "+(String)oneTask.objArg1 );
					dbManager.clearColumnFilters((String)oneTask.objArg1);
					nurse.set();
					break;
				case DBdefine.MSG.MSG_TASK_CHANGE_COLUMN_VISIBLE:
					dbManager.updateColumnVisibility((ArrayList<DBdefine.HeaderItem>)oneTask.objArg1);
					nurse.set();
					break;
				}
			}
		} catch (InterruptedException intExp) {
			return;		// Just finish the thread.
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/** To output all the data to system screen */
/*	public void printData() {
		if (headerList == null)
			return;
		StringBuilder sbd = null;
		int iHeader, nHeader = headerList.size();
		for (iHeader = 0; iHeader < nHeader; iHeader++) {
			DBdefine.HeaderItem hi = headerList.get(iHeader);
			sbd = new StringBuilder();
			sbd.append("HeaderList["+iHeader+"]: ");
			sbd.append("Name = " + hi.Name + ", ");
			sbd.append("Title = " + hi.Title + ", ");
			sbd.append("Type = " + hi.Type + ", ");
			sbd.append("Width = " + hi.Width + ", ");
		//	sbd.append("Show = " + hi.Visible);
		}
		Log.i("DeviceList.printData()", sbd.toString());	// Show the last line
		
		int iDevice, nDevice = deviceList.size();
		for (iDevice = 0; iDevice < nDevice; iDevice++) {
			DBdefine.DeviceRow dev = deviceList.get(iDevice);
			sbd = new StringBuilder();
			sbd.append("DeviceList["+iDevice+"]: ");
			for (iHeader = 0; iHeader < nHeader; iHeader++) {
				DBdefine.HeaderItem hi = headerList.get(iHeader);
				sbd.append(hi.Name +" = "+ dev.Param[iHeader]+"; ");
			}
		}
		Log.i("DeviceList.printData()", sbd.toString());	// Show the last line
	}
*/
	public static void addTask(int taskMsg, Messenger mMessenger, boolean isblock, Object arg1, Object arg2, Object arg3) throws InterruptedException {
		Task oneTask = new Task();
		oneTask.msg = taskMsg;
		oneTask.block = isblock;
		oneTask.objArg1 = arg1;
		oneTask.objArg2 = arg2;
		oneTask.objArg3 = arg3;
		oneTask.replyto = mMessenger;
		taskQueue.put(oneTask);
		
		if (isblock)
			nurse.get();
	}
	

    private class waitingRoom {
    	boolean gotten = false;
    	public synchronized void get() {
    		if (gotten == false)
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		gotten = false;
    		notify();
    	}
    	public synchronized void set() {
    		if (gotten)
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		gotten = true;
    		notify();
    	}
    }

}
