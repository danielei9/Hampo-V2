package com.example.hampo;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class AdaptadorImagenesB extends AdaptadorImagenes {
    private final Context context;

    public AdaptadorImagenesB(Context context, @NonNull FirestoreRecyclerOptions<Imagen> options) {
        super(context, options);
        this.context = context;
    }
    @Override
    public com.example.hampo.AdaptadorImagenes.ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.imagen_b_galeria_item, parent, false);
        return new com.example.hampo.AdaptadorImagenes.ViewHolder(view);
    }
   /* @Override
    protected void onBindViewHolder(@NonNull final com.example.hampo.AdaptadorImagenesB
            .ViewHolder holder, final int position, @NonNull final Imagen imagen) {
        holder.titulo.setText(imagen.getTitulo());
        CharSequence prettyTime = DateUtils.getRelativeDateTimeString(
                context, imagen.getTiempo(), DateUtils.SECOND_IN_MILLIS,
                DateUtils.WEEK_IN_MILLIS, 0);
        holder.tiempo.setText(prettyTime);
        Glide.with(context)
                .load(imagen.getUrl())
                .into(holder.imagen);
        holder.itemView.setOnClickListener(onClickListener);
        holder.eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagen.getTitulo();
                Log.e("TAG", "tittle: " + imagen.getTitulo());

            }
        });
    }*/
        @Override
        public void setOnItemClickListener (View.OnClickListener onClick){
            onClickListener = onClick;
            Log.e("CLICK", "Click to" + onClick);
        }


}
