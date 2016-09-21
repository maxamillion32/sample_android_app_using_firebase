package android.freelessons.org.sampleandroidappusingfirebase;

import android.app.DialogFragment;
import android.content.Intent;
import android.freelessons.org.sampleandroidappusingfirebase.domain.Event;
import android.freelessons.org.sampleandroidappusingfirebase.ui.EventActivity;
import android.freelessons.org.sampleandroidappusingfirebase.ui.SignInUI;
import android.freelessons.org.sampleandroidappusingfirebase.ui.SignUpUI;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActionBarActivity {

    static String TAG="SAPWF";
    List<Event> events=new ArrayList<>();
    @InjectView(R.id.eventsList) ListView listView;
    @InjectView(R.id.addEvent)
    FloatingActionButton addEvent;
    @InjectView(R.id.signIn)
    Button signIn;
    @InjectView(R.id.signUp) Button signUp;
    @InjectView(R.id.signOut) Button signOut;
    EventListAdapter eventListAdapter=new EventListAdapter();
    DatabaseReference databaseReference;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference=FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                updateViews();
            }
        };
        firebaseAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    private void addEvent(){
        if(firebaseAuth.getCurrentUser()!=null){
            Intent intent=new Intent(getApplicationContext(),EventActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(MainActivity.this, "Please sign in to create an event", Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    protected void onResume() {
        events=new ArrayList<>();
        super.onResume();
        listView.setAdapter(eventListAdapter);
        DatabaseReference eventsReference=databaseReference.child("events");
        eventsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Event event=new Event();
                event.setName(dataSnapshot.child(Event.NAME_PROPERTY).getValue().toString());
                event.setDescription(dataSnapshot.child(Event.DESCRIPTION_PROPERTY).getValue().toString());
                event.setLocation(dataSnapshot.child(Event.LOCATION_PROPERTY).getValue().toString());
                event.setEventId(dataSnapshot.child(Event.EVENT_ID_PROPERTY).getValue().toString());
                if(dataSnapshot.child(Event.START_DATE_PROPERTY).getValue()!=null){
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(Long.parseLong(dataSnapshot.child(Event.START_DATE_PROPERTY).getValue().toString()));
                    event.setStartDate(calendar.getTime());
                }

                events.add(event);
                eventListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEvent();
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
        updateViews();
    }
    private void updateViews(){
        if(firebaseAuth.getCurrentUser()!=null){
            signIn.setVisibility(View.GONE);
            signOut.setVisibility(View.VISIBLE);
            signUp.setVisibility(View.GONE);
        }else{
            signIn.setVisibility(View.VISIBLE);
            signOut.setVisibility(View.GONE);
            signUp.setVisibility(View.VISIBLE);
        }
    }
    private void signIn(){
        DialogFragment dialog=SignInUI.newInstance();
        dialog.show(getFragmentManager(),"SignInUI");
    }
    private void signUp(){
        DialogFragment dialog=SignUpUI.newInstance();
        dialog.show(getFragmentManager(),"SignUpUI");
    }
    private void signOut(){
        firebaseAuth.signOut();
    }
    class EventListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return events.size();
        }

        @Override
        public Object getItem(int i) {
            return events.get(i);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View eventRow = layoutInflater.inflate(R.layout.event_row,null);
            TextView eventName = (TextView)eventRow.findViewById(R.id.eventName);
            Event event = (Event) getItem(i);
            eventName.setText(event.getName());
            TextView eventDate = (TextView)eventRow.findViewById(R.id.eventDate);
            eventDate.setText(simpleDateFormat.format(event.getStartDate()));
            TextView eventLocation = (TextView)eventRow.findViewById(R.id.eventLocation);
            eventLocation.setText(event.getLocation());
            TextView eventDescription = (TextView)eventRow.findViewById(R.id.eventDescription);
            eventDescription.setText(event.getDescription());
            return eventRow;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }
    }
}
