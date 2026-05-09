package com.example.thequality.homepage.riwayat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thequality.R;
import com.example.thequality.homepage.HasilDeteksi;

import java.util.ArrayList;


public class RiwayatAdapter extends RecyclerView.Adapter<RiwayatAdapter.ViewHolder> {

    private ArrayList<HasilDeteksi> list;

    public RiwayatAdapter(ArrayList<HasilDeteksi> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_riwayat, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HasilDeteksi data = list.get(position);

        holder.tvTanggal.setText("Tanggal: " + data.getTanggal());
        holder.tvSekolah.setText("Sekolah: " + data.getSekolah());

        // PPB (konsentrasi gas)
        holder.tvCo2.setText("PPB: " + data.getPpb());

        // RGB/Gray (kalau mau ditampilkan)
        holder.tvBakteri.setText("Warna RGB: " + data.getRgb());

        // Status layak
        String status = data.getPpb() < 400000 ? "LAYAK" : "TIDAK LAYAK";
        holder.tvStatus.setText("Status: " + status);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTanggal, tvSekolah, tvCo2, tvBakteri, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTanggal = itemView.findViewById(R.id.tvTanggal);
            tvSekolah = itemView.findViewById(R.id.tvSekolah);
            tvCo2 = itemView.findViewById(R.id.tvCo2);
            tvBakteri = itemView.findViewById(R.id.tvBakteri);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
