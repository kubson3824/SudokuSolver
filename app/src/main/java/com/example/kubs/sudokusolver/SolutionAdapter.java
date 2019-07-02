package com.example.kubs.sudokusolver;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SolutionAdapter extends BaseAdapter {
    private Context context;
    private Integer[][] values;

    public SolutionAdapter(Context context, Integer[][] values){
        this.context = context;
        this.values = values;
    }

    @Override
    public int getCount() {
        if(values != null)
            return values.length*values[0].length;
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null){
            gridView = new View(context);
            gridView = inflater.inflate(R.layout.solved_element,null);
            final TextView textView = gridView.findViewById(R.id.solvedText);
            if(values[position/9][position%9] == null){
                textView.setText("");
            }
            else {
                if (values[position / 9][position % 9] > 0)
                    textView.setText(String.valueOf((values[position / 9][position % 9])));
                else
                    textView.setText("");
            }
        }
        else{
            gridView = convertView;
        }
        return gridView;
    }
}
