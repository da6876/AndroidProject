package bd.com.arda.findyourhome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import bd.com.arda.findyourhome.R;
import bd.com.arda.findyourhome.model.MyToletListModel;


public class MyToletAdapter extends ArrayAdapter<MyToletListModel> {

    ArrayList<MyToletListModel> myToletListModels;
    Context context;
    public MyToletAdapter(@NonNull Context context, int resource, ArrayList<MyToletListModel> myToletListModels) {
        super(context, R.layout.tolet_list_adapter, myToletListModels);
        this.context = context;
        this.myToletListModels = myToletListModels;
    }
    private static class ViewHolder{
        TextView tv_tolet_name,tv_avalable_date,tv_tolet_type,tv_tolet_details,tv_address,
                tv_Bath,tv_Beds,tv_Floors;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;
        final View result;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.my_tolet_list_adapter,parent,false);
            holder.tv_tolet_name = (TextView) convertView.findViewById(R.id.tv_tolet_name);
            holder.tv_avalable_date = (TextView) convertView.findViewById(R.id.tv_avalable_date);
            holder.tv_tolet_type = (TextView) convertView.findViewById(R.id.tv_tolet_type);
            holder.tv_tolet_details = (TextView) convertView.findViewById(R.id.tv_tolet_details);
            holder.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
            holder.tv_Bath = (TextView) convertView.findViewById(R.id.tv_Bath);
            holder.tv_Beds = (TextView) convertView.findViewById(R.id.tv_Beds);
            holder.tv_Floors = (TextView) convertView.findViewById(R.id.tv_Floors);
            result = convertView;
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        holder.tv_tolet_name.setText(myToletListModels.get(position).getTOLET_NAME());
        holder.tv_avalable_date.setText("Available Date : "+myToletListModels.get(position).getAVAILABLE_FROM());
        holder.tv_tolet_type.setText("Tolet Type : "+myToletListModels.get(position).getTOLET_TYPE_NAME());
        holder.tv_tolet_details.setText("Details : "+myToletListModels.get(position).getTOLET_DETAILS());
        holder.tv_address.setText("Address : "+myToletListModels.get(position).getADDRESS());
        holder.tv_Bath.setText("Bath : "+myToletListModels.get(position).getBATHS());
        holder.tv_Beds.setText("Bed : "+myToletListModels.get(position).getBEDS());
        holder.tv_Floors.setText("Floors : "+myToletListModels.get(position).getFLOORS());
        return result;
    }
}
