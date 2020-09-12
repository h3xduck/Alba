package com.marsanpat.alba.ui.home;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.marsanpat.alba.Controller.MessageController;
import com.marsanpat.alba.R;
import com.marsanpat.alba.ui.logs.LogFragment;
import com.marsanpat.alba.ui.logs.LogViewModel;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this, new HomeFragment.MyViewModelFactory(this.getActivity().getApplication())).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final ImageView serverConnectionImage = root.findViewById(R.id.serverConnectionImage);
        homeViewModel.getConnectionState().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    //Connection is set.
                    serverConnectionImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(), android.R.drawable.presence_online, null));
                }else{
                    //No connection to server
                    serverConnectionImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(), android.R.drawable.presence_offline, null));
                }
            }
        });


        ImageButton refreshButton = (ImageButton) root.findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.d("debug", "Requested manual connection to the server");
                            RotateAnimation rotateAnimation = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                            rotateAnimation.setRepeatCount(Animation.INFINITE);
                            rotateAnimation.setRepeatMode(Animation.RESTART);
                            rotateAnimation.setDuration(1000);
                            refreshButton.startAnimation(rotateAnimation);
                            MessageController controller = MessageController.getInstance();
                            controller.startClient();
                            Thread.sleep(4000);
                            if (controller.getLiveClientState()!=null && controller.getLiveClientState().getValue()) {
                                showToast("Connected successfully.");
                            } else {
                                showToast("Could not connect to server.");
                            }
                            refreshButton.clearAnimation();
                        } catch (InterruptedException e) {
                            Log.d("debug", "Refresh thread interrumpted...");
                            e.printStackTrace();
                        }

                    }
                });
                thread.start();

            }
        });

        ImageButton disconnectButton = (ImageButton) root.findViewById(R.id.disconnectButton);
        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("debug", "Disconnecting from server");
                        MessageController controller = MessageController.getInstance();
                        if(controller.isClientActive()){
                            controller.disconnectFromServer();
                        }
                    }
                });
                thread.start();

            }
        });


        return root;
    }

    public static class MyViewModelFactory implements ViewModelProvider.Factory {
        private final Application myApplication;

        public MyViewModelFactory(Application application) {
            this.myApplication = application;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new HomeViewModel(myApplication);
        }
    }

    public void showToast(final String toast)
    {
        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), toast, Toast.LENGTH_LONG).show());
    }
}