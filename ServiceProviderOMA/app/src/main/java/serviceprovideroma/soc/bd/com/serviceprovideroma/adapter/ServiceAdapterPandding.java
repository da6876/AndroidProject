package serviceprovideroma.soc.bd.com.serviceprovideroma.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import serviceprovideroma.soc.bd.com.serviceprovideroma.R;
import serviceprovideroma.soc.bd.com.serviceprovideroma.model.ServiceListPandding;

public class ServiceAdapterPandding extends ArrayAdapter<ServiceListPandding> {
    ArrayList<ServiceListPandding> customers;
    Context context;
    public ServiceAdapterPandding(@NonNull Context context, int resource, ArrayList<ServiceListPandding> customers) {
        super(context, R.layout.service_page_pandding, customers);
        this.context = context;
        this.customers = customers;
    }
    private static class ViewHolder{
        TextView tv_serviceName,tv_status,tv_tokenNumber,tv_amnout,tv_address,tv_userName,tv_phone,tv_email;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;
        final View result;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.service_page_pandding,parent,false);
            holder.tv_serviceName = (TextView) convertView.findViewById(R.id.tv_serviceName);
            holder.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
            holder.tv_tokenNumber = (TextView) convertView.findViewById(R.id.tv_tokenNumber);
            holder.tv_amnout = (TextView) convertView.findViewById(R.id.tv_amnout);
            holder.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
            holder.tv_userName = (TextView) convertView.findViewById(R.id.tv_userName);
            holder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
            holder.tv_email = (TextView) convertView.findViewById(R.id.tv_email);
            result = convertView;
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        holder.tv_serviceName.setText("Service Name : "+customers.get(position).getSERVICE_TYPE_NAME());
        holder.tv_status.setText("Status : "+customers.get(position).getSERVICE_STATUS());
        holder.tv_tokenNumber.setText("Token No : "+customers.get(position).getTOKEN_NO());
        holder.tv_amnout.setText("Amount : "+customers.get(position).getSERVICE_CHARGE());
        holder.tv_address.setText("Address : "+customers.get(position).getUSER_ADDRESS());
        holder.tv_userName.setText("Name : "+customers.get(position).getUSER_INFO_NAME());
        holder.tv_phone.setText("Phone : "+customers.get(position).getUSER_PHONE());
        holder.tv_email.setText("Mail : "+customers.get(position).getUSER_EMAIL());
        return result;
    }
}
