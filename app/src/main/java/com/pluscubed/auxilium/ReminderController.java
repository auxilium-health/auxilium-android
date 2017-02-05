package com.pluscubed.auxilium;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.pluscubed.auxilium.base.RefWatchingController;
import com.tomerrosenfeld.customanalogclockview.CustomAnalogClock;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ReminderController extends RefWatchingController {

    @BindView(R.id.clock)
    CustomAnalogClock clock;
    @BindView(R.id.fab)
    FloatingActionButton button;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private List<Medication> medications;

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.view_clock, container, false);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        clock.init(getActivity(), R.drawable.face, R.drawable.default_hour_hand, R.drawable.face, 255, true, true);
        clock.setAutoUpdate(true);

        toolbar.setTitle("Auxilium");

        button.setOnClickListener(v -> {
            AddMedicationController controller = new AddMedicationController();
            controller.setTargetController(this);
            getRouter().pushController(RouterTransaction.with(controller)
                    .tag("addMedication")
                    .popChangeHandler(new FadeChangeHandler())
                    .pushChangeHandler(new FadeChangeHandler()));
        });

        medications = new ArrayList<>();

        for (Medication medication : medications) {

        }
    }


    public void addMedication(Integer integer) {
        Medication e = new Medication();
        e.seconds = integer;
        medications.add(e);
    }
}
