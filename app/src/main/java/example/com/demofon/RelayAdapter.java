package example.com.demofon;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RelayAdapter extends RecyclerView.Adapter<RelayHolder> {
    LayoutInflater inflater;
    List<RelayModel> list;
    IsApi api;

    public RelayAdapter(Context context, List<RelayModel> list, IsApi api) {
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.api = api;
    }

    @NonNull
    @Override
    public RelayHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.adress_item, viewGroup,false);
        return new RelayHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RelayHolder holder, int i) {
        holder.onBind(list.get(i),api);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
class RelayHolder extends RecyclerView.ViewHolder{
    Button open;
    TextView adress;

    public RelayHolder(@NonNull View view) {
        super(view);
        open = view.findViewById(R.id.btn_open);
        adress = view.findViewById(R.id.tv_address);
    }

    public void onBind(final RelayModel relay, final IsApi api) {
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                api.open(relay.id).enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {

                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {

                    }
                });
            }
        });
        adress.setText(relay.address);
    }
}
