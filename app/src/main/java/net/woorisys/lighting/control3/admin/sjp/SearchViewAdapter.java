package net.woorisys.lighting.control3.admin.sjp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import net.woorisys.lighting.control3.admin.R;

import java.util.ArrayList;
import java.util.List;

public class SearchViewAdapter  extends ArrayAdapter<String> {


    private List<String> SearchViewList;

    public SearchViewAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);

        this.SearchViewList=new ArrayList<>(objects);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }

    @NonNull
    @Override
    public View getView(int position,  @Nullable View convertView,  @NonNull ViewGroup parent) {

        if(convertView==null)
        {
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.searchview_listview_item,parent,false);
        }

        TextView textView=convertView.findViewById(R.id.txt_Search);

        String value=getItem(position);

        if(value!=null)
        {
            textView.setText(value);
        }
        return convertView;
    }

    private Filter filter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results=new FilterResults();
            List<String> resultList=new ArrayList<>();

            if(constraint==null || constraint.length()==0)
            {
                resultList.addAll(SearchViewList);
            }
            else
            {
                String filterPattern=constraint.toString().toLowerCase().trim();

                for(String Item: SearchViewList)
                {
                    if(Item.contains(filterPattern))
                    {
                        resultList.add(Item);
                    }
                }
            }

            results.values=resultList;
            results.count=resultList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List)results.values);

            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((String)resultValue);
        }
    };
}
