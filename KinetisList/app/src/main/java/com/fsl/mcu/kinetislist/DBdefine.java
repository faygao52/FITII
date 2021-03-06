package com.fsl.mcu.kinetislist;

import android.provider.BaseColumns;

public final class DBdefine {
    // This class cannot be instantiated
    private DBdefine() {
    }

    public static final class MSG {
	    public static final int MSG_TASK_LIST_FAMILIES = 1;
	    public static final int MSG_TASK_LIST_HEADERS = 2;
	    public static final int MSG_TASK_LIST_CONTENTS = 3;
	    /** To get a list of visible values of selected column.
	     * <br>Arg1 = Family; Arg2 = Column */
	    public static final int MSG_TASK_LIST_COLUMN_VALUES = 4;
	    public static final int MSG_TASK_CHANGE_COLUMN_VALUES = 5;
	    public static final int MSG_TASK_LIST_COLUMN_FILTERS = 6;
	    public static final int MSG_TASK_CLEAR_COLUMN_FILTERS = 7;
	    public static final int MSG_TASK_CHANGE_COLUMN_VISIBLE = 8;
	    public static final int MSG_FILL_TABLE_LINE = 10;
	    
	    public static final int MSG_INIT_SCROLL_POSITION = 11;
	    public static final int MSG_LAST_POSITION = 12;
		public static final int MSG_TASK_LIST_SOURCES = 13;
    }
    
    public static class HeaderItem {
    	String Name;
    	String Title;
    	String Type;
    	int Width;
    	boolean Visible;
    }
    public static final class xHeaderItem extends HeaderItem {
    	boolean Visible;   	
    }
    public static final class DeviceRow {
    	String[] Param;
    	boolean Visible;
    	public DeviceRow(int nColumn) {
    		Param = new String[nColumn];
    		Visible = true;
    	}
    }

    /** This class is used for transfer data array from/to getColumnValues() and setColumnValues().
     * @param Text : String
     * @param Value : int
     * @param Visible : boolean */
    public static final class ColumnValue {
    	String Text;
    	int Value;
    	boolean Visible;
    }
    /** This class is used for transfer data array from/to getColumnFilters() and clearColumnFiltesr().
    * @param Column : String
    * @param Visible : boolean */
    public static final class ColumnFilter {
    	String Column;
    	boolean Visible;
    }
  
	/** Header table contract: This table is used to record each header item attributes */
    public static final class Header implements BaseColumns {

        // This class cannot be instantiated
        private Header() {}

        // The table name offered by this provider
        public static final String TABLE_NAME = "Header";


        // The default sort order for this table
        public static final String DEFAULT_SORT_ORDER = "_id ASC";


        // Column definitions

        // Column name for the name of the header
        // <P>Type: TEXT</P>
        public static final String COLUMN_NAME_NAME = "Name";

        // Column name for the title of the header
        // <P>Type: TEXT</P>
        public static final String COLUMN_NAME_TITLE = "Title";

        // Column name of the "type of column" of the header
        // <P>Type: TEXT</P>
        public static final String COLUMN_NAME_TYPE = "Type";

        // Column name for the "width of column" of the header
        // <P>Type: INTEGER</P>
        public static final String COLUMN_NAME_WIDTH = "Width";

        // Column name for the "visibility of the column" of the header
        // <P>Type: BOOLEAN</P>
        public static final String COLUMN_NAME_SHOW = "Show";
    }

	/** Column table contract: This table is used to record the filter setting of each column */
    public static final class Column implements BaseColumns {

        // This class cannot be instantiated
        private Column() {}

        // The table name offered by this provider
        public static final String TABLE_NAME = "Column";

        // The default sort order for this table
        public static final String DEFAULT_SORT_ORDER = "_id ASC";


        // Column definitions

        // Column name for the "Column". This field corresponds to the Header table of COLUMN_NAME_NAME.
        // <P>Type: TEXT</P>
        public static final String COLUMN_NAME_NAME = "Column";

        // Column name of the "type of column" of the header. This field corresponds to the Header table of COLUMN_NAME_TYPE
        // <P>Type: TEXT</P>
        public static final String COLUMN_NAME_TYPE = "Type";
       
        // Column name for the content of the item if it is a string
        // <P>Type: TEXT</P>
        public static final String COLUMN_NAME_TEXT = "Text";

        // Column name of the content of the item if it is an integer
        // <P>Type: INTEGER</P>
        public static final String COLUMN_NAME_VALUE = "Value";

        // Column name for the inclusive status of the item
        // <P>Type: BOOLEAN</P>
        public static final String COLUMN_NAME_SHOW = "Show";
    }

	/** Devices table contract: This table is used to record the contents of each row (device) */
    public static final class Devices implements BaseColumns {

        // This class cannot be instantiated
        private Devices() {}

        // The table name offered by this provider
        public static final String TABLE_NAME = "Devices";

        // The default sort order for this table
        public static final String DEFAULT_SORT_ORDER = "`MK Part Number` ASC";

        // Column definitions
        // Column names are dynamically generated from the import XML
    }
}