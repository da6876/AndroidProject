package oma.soc.bd.com.omanmotorassistance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import oma.soc.bd.com.omanmotorassistance.model.ServiceList;

public class ServiceAdapter extends ArrayAdapter<ServiceList> {
    ArrayList<ServiceList> customers;
    Context context;
    public ServiceAdapter(@NonNull Context context, int resource, ArrayList<ServiceList> customers) {
        super(context, R.layout.service_adapter, customers);
        this.context = context;
        this.customers = customers;
    }
    private static class ViewHolder{
        TextView textcomplain;
        TextView textstatus;
        TextView textcustomerNumber;
        TextView serviceBy;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;
        final View result;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.service_adapter,parent,false);
            holder.textcomplain = (TextView) convertView.findViewById(R.id.complain);
            holder.textstatus = (TextView) convertView.findViewById(R.id.status);
            holder.textcustomerNumber = (TextView) convertView.findViewById(R.id.customerNumber);
            holder.serviceBy = (TextView) convertView.findViewById(R.id.serviceBy);
            result = convertView;
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        holder.textcomplain.setText(customers.get(position).getServiceName());
        holder.textstatus.setText("Status : "+customers.get(position).getServiceStatus());
        holder.textcustomerNumber.setText("Price : "+customers.get(position).getServiceprice());
        holder.serviceBy.setText("Service By : "+customers.get(position).getServiceByName());
        return result;
    }
}
