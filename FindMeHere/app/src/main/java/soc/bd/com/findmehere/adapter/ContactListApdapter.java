package soc.bd.com.findmehere.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import soc.bd.com.findmehere.R;
import soc.bd.com.findmehere.model.ContactListModel;

public class ContactListApdapter extends ArrayAdapter<ContactListModel> {

    ArrayList<ContactListModel> customers;
    Context context;
    public ContactListApdapter(Context context, int resource, ArrayList<ContactListModel> customers) {
        super(context, R.layout.contact_list_apdapter, customers);
        this.context = context;
        this.customers = customers;
    }
    private static class ViewHolder{
        TextView name;
        TextView number;
        TextView tv_FastWord;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;
        final View result;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.contact_list_apdapter,parent,false);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.number = (TextView) convertView.findViewById(R.id.number);
            holder.tv_FastWord = (TextView) convertView.findViewById(R.id.tv_FastWord);
            result = convertView;
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        holder.tv_FastWord.setText(customers.get(position).getName().toString().toUpperCase().substring(0,1));
        holder.name.setText(customers.get(position).getName());
        holder.number.setText(customers.get(position).getPhoneNumber());
        return result;
    }
}