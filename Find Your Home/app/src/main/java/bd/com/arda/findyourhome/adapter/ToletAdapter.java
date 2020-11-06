package bd.com.arda.findyourhome.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import bd.com.arda.findyourhome.R;
import bd.com.arda.findyourhome.model.ToletListModel;


public class ToletAdapter extends ArrayAdapter<ToletListModel> {

    ArrayList<ToletListModel> toletListModels;
    Context context;
    public static Bitmap bitmapCropImage=null;
    public ToletAdapter(@NonNull Context context, int resource, ArrayList<ToletListModel> toletListModels) {
        super(context, R.layout.tolet_list_adapter, toletListModels);
        this.context = context;
        this.toletListModels = toletListModels;
    }
    private static class ViewHolder{
        TextView tv_tolet_name,tv_avalable_date,tv_tolet_type,tv_tolet_details,tv_address,
                tv_Bath,tv_Beds,tv_Floors;
        ImageView iv_toletImage;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;
        final View result;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.tolet_list_adapter,parent,false);
            holder.iv_toletImage = (ImageView) convertView.findViewById(R.id.iv_toletImage);
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

        String temp = toletListModels.get(position).getPRODUCT_IMAGE();
        if(temp.equals("null") || temp.equals(null)){
            holder.iv_toletImage.setImageResource(R.drawable.dummy_house);
        }else {
            byte [] encodeByte= Base64.decode(temp, Base64.DEFAULT);
            bitmapCropImage= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            holder.iv_toletImage.setImageBitmap(bitmapCropImage);
        }
        holder.tv_tolet_name.setText(toletListModels.get(position).getTOLET_NAME());
        holder.tv_avalable_date.setText("Available Date : "+toletListModels.get(position).getAVAILABLE_FROM());
        holder.tv_tolet_type.setText("Tolet Type : "+toletListModels.get(position).getTOLET_TYPE_NAME());
        holder.tv_tolet_details.setText("Details : "+toletListModels.get(position).getTOLET_DETAILS());
        holder.tv_address.setText("Address : "+toletListModels.get(position).getADDRESS());
        holder.tv_Bath.setText("Bath : "+toletListModels.get(position).getBATHS());
        holder.tv_Beds.setText("Bed : "+toletListModels.get(position).getBEDS());
        holder.tv_Floors.setText("Floors : "+toletListModels.get(position).getFLOORS());
        return result;
    }
}
