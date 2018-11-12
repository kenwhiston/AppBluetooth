package pe.edu.bitec.appbluetooth;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterItemBluetooth extends RecyclerView.Adapter<AdapterItemBluetooth.ViewHolder>
        implements View.OnClickListener{

    private ArrayList<ItemBluetooth> data;

    public AdapterItemBluetooth(ArrayList<ItemBluetooth> data) {
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.ly_item_bluetooth,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemBluetooth itemBluetooth = this.data.get(position);

        holder.txtName.setText(itemBluetooth.getName());
        holder.txtNumber.setText(itemBluetooth.getNumber());

        //agregar el oyente
        holder.lyFondo.setOnClickListener(this);
        holder.lyFondo.setTag(position);
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.lyFondo){
            int pos = Integer.parseInt(view.getTag().toString());
            ItemBluetooth itemBluetooth = data.get(pos);
            Intent intent = new Intent(view.getContext(), DatosEnviadosActivity.class);
            //Intent intent = new Intent(view.getContext(), DatosRecibidosActivity.class);
            intent.putExtra("ITEM",itemBluetooth);
            view.getContext().startActivity(intent);
        }
    }

    //subclase
    class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout lyFondo;
        TextView txtName;
        TextView txtNumber;

        public ViewHolder(View itemView) {

            super(itemView);

            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtNumber = (TextView) itemView.findViewById(R.id.txtNumber);
            lyFondo = (LinearLayout) itemView.findViewById(R.id.lyFondo);

        }
    }

}
