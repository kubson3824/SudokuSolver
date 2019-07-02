package com.example.kubs.sudokusolver;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;

public class BoardAdapter extends BaseAdapter {
    private Context context;
    private Integer[][] values;

    public BoardAdapter(Context context, Integer[][] values){
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
            gridView = inflater.inflate(R.layout.number_element,null);
            final EditText editText = gridView.findViewById(R.id.numberText);
            if(values[position/9][position%9] == null){
                editText.setText("");
            }
            else {
                if (values[position / 9][position % 9] > 0)
                    editText.setText(String.valueOf((values[position / 9][position % 9])));
                else
                    editText.setText("");
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (editText.getText().toString().equals("") || Integer.parseInt(editText.getText().toString()) > 9 || Integer.parseInt(editText.getText().toString()) < 1) {
                            values[position / 9][position % 9] = 0;
                        } else{
                            values[position / 9][position % 9] = Integer.parseInt(editText.getText().toString());
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(!hasFocus && !editText.getText().toString().equals("")) {
                            if (Integer.parseInt(editText.getText().toString()) > 9 || Integer.parseInt(editText.getText().toString()) < 1) {
                                editText.setText("");
                            }
                        }
                    }
                });
            }
        }
        else{
            gridView = convertView;
        }
        return gridView;
    }
}
