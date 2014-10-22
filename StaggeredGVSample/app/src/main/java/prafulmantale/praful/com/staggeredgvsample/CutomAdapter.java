package prafulmantale.praful.com.staggeredgvsample;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.etsy.android.grid.util.DynamicHeightTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by prafulmantale on 10/13/14.
 */
public class CutomAdapter extends ArrayAdapter<String> {

    ArrayList<Integer> colorsList;
    private static final StrikethroughSpan STRIKE_THROUGH_SPAN = new StrikethroughSpan();

    private class ViewHolder{
        DynamicHeightTextView txtItem;
        TextView txtTitle;
    }

    public CutomAdapter(Context context, List<String> objects) {
        super(context, R.layout.item_list, objects);

        colorsList = new ArrayList<Integer>();
        colorsList.add(R.color.orange);
        colorsList.add(R.color.green);
        colorsList.add(R.color.blue);
        colorsList.add(R.color.yellow);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        String listName = getItem(position);

        if(convertView == null){

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_list, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.txtTitle = (TextView)convertView.findViewById(R.id.txtTitle);
            viewHolder.txtItem = (DynamicHeightTextView)convertView.findViewById(R.id.txtItemList);

            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        int backgroundIndex = position >= colorsList.size() ?
                position % colorsList.size() : position;

        //convertView.setBackgroundColor(colorsList.get(backgroundIndex));
        StateListDrawable sld = (StateListDrawable)convertView.getBackground();
        GradientDrawable gd = (GradientDrawable)sld.getCurrent();
        gd.setColor(getContext().getResources().getColor(colorsList.get(backgroundIndex)));


        List<String> list = MainActivity.seedData.get(listName);
        viewHolder.txtItem.setText("");
        int i = 0;
        for(String str : list){
            if(i%2 == 0){
              int start = viewHolder.txtItem.getText().toString().length();
                viewHolder.txtItem.append(str + "\r\n");
                Spannable spannable = (Spannable) viewHolder.txtItem.getText();
                spannable.setSpan(STRIKE_THROUGH_SPAN, start, start + str.length() -1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


            }
            else {
                viewHolder.txtItem.append(str + "\r\n");
            }
//            viewHolder.txtItem.setPaintFlags(viewHolder.txtItem.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            i++;

        }



        viewHolder.txtTitle.setText(listName);

        return convertView;
    }
}
