package com.example.qidian;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class EquationAdapter extends ArrayAdapter<Equation> {
    private Context _context;
    private int _resouceID;
    private List<Equation> _data = new ArrayList<>();

    public EquationAdapter(Context context, List<Equation> objects) {
        this(context, R.layout.equation_item, objects);
    }

    public EquationAdapter(Context context, int resourceId, List<Equation> objects) {
        super(context, resourceId, objects);
        _context = context;
        _resouceID = resourceId;
        _data = objects;
    }

    public int getErrorCount(){
        int errorCount =0;
        for (Equation equation:
             _data) {
           if(!equation.getIsSuccess()){
               errorCount++;
           }
        }
        return errorCount;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(_context).inflate(_resouceID, parent, false);
        } else {
            view = convertView;
        }
        TextView textView_a = view.findViewById(R.id.equation_a);
        textView_a.setText(_data.get(position).getA() + "");
        TextView textView_c = view.findViewById(R.id.equation_c);
        switch (_data.get(position).getSign()) {
            case 0:
                textView_c.setText("+");
                break;
            case 1:
                textView_c.setText("-");
                break;
            case 2:
                textView_c.setText("×");
                break;
            case 3:
                textView_c.setText("÷");
                break;
        }
        TextView textView_b = view.findViewById(R.id.equation_b);
        textView_b.setText(_data.get(position).getB() + "");
        TextView textView_r = view.findViewById(R.id.equation_r);
        if(_data.get(position).getSign()==3){
            textView_r.setText(_data.get(position).getTmpChuFaResult().ChuShu + "......"+
                    _data.get(position).getTmpChuFaResult().YuShu);
        }else {
            textView_r.setText(_data.get(position).getTmpResult() + "");
        }
        if (!_data.get(position).getIsSuccess()) {
            view.findViewById(R.id.imageView_err).setVisibility(View.VISIBLE);
            view.findViewById(R.id.imageView_right).setVisibility(View.GONE);
        }else{
            view.findViewById(R.id.imageView_err).setVisibility(View.GONE);
            view.findViewById(R.id.imageView_right).setVisibility(View.VISIBLE);
        }
        return view;
    }
}
