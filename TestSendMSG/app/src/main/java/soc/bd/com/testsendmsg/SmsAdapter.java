package soc.bd.com.testsendmsg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;


public class SmsAdapter extends BaseAdapter{

    ArrayList<SmsModel> smsModelArrayList;
    Context context;
    private static LayoutInflater inflater=null;;

    public SmsAdapter(MainActivity mainActivity, ArrayList<SmsModel> smsModelArrayList) {
// TODO Auto-generated constructor stub

        context=mainActivity;
        this.smsModelArrayList = smsModelArrayList;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
// TODO Auto-generated method stub
        return smsModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
// TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
// TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView tv_mobile;
        TextView tv_message;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
// TODO Auto-generated method stub
        Holder holder=new Holder();
        View view;
        SmsModel item = smsModelArrayList.get(position);
        view = inflater.inflate(R.layout.layout_sms_item, null);

        holder.tv_mobile = (TextView) view.findViewById(R.id.tv_mobile);
        holder.tv_message = (TextView) view.findViewById(R.id.tv_message);

        holder.tv_mobile.setText(item.getMobile());
        holder.tv_message.setText(item.getMessage());

        return view;
    }


}