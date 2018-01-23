package org.udg.pds.todoandroid.fragment.taskListView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.Task;
import org.udg.pds.todoandroid.util.Global;

// This class is responsible of updating the Tasks list
// Tutorial about ArrayAdapter and ListView: http://www.ezzylearning.com/tutorial.aspx?tid=1763429
public class TLVAdapter extends ArrayAdapter<Task> {

    private LayoutInflater mInflater = null;

    //Initialize adapter
    public TLVAdapter(Context context, int mResource) {
        super(context, mResource);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
     
    // This is the main function
    // It generates a View for each Task that we want to show in the list
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LinearLayout alertView;
        //Get the current alert object
        Task t = getItem(position);
         
        View vi = convertView;
        //Inflate the view if there is no other View (convertView) that the system gives us to "recycle"
        if(convertView==null)
        {
        	vi = mInflater.inflate(R.layout.task_layout, null);
        }
        //Get the text boxes
        TextView tvDateL =(TextView)vi.findViewById(R.id.taskitem_date_limit);
        TextView tvText =(TextView)vi.findViewById(R.id.taskitem_title);
         
        // Assign the appropriate data from our task object above
        tvDateL.setText(Global.TIME_DATE_FORMAT.format(t.dateLimit));
        tvText.setText(t.text);
         
        return vi;
    }
 
}