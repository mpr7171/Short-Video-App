package com.example.shortvideoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.shortvideoapp.adapter.VideoItemAdapter
import com.example.shortvideoapp.model.Video
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val videosViewPager:ViewPager2 = findViewById<ViewPager2>(R.id.viewPagerVideos)


        val videosURlList = mutableListOf<Video>()
        getVideoUrlsFromFirebase(videosURlList)
        videosViewPager.adapter = VideoItemAdapter(this,videosURlList);
    }

    private fun getVideoUrlsFromFirebase(videos: MutableList<Video>) {
        val dbReference = FirebaseDatabase.getInstance().getReference("Videos")
        dbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val id = snapshot.child("id").value.toString()
                    val title = snapshot.child("title").value.toString()
                    val timestamp = snapshot.child("timestamp").value.toString()
                    val videoUri = snapshot.child("videoUri").value.toString()
                    val video = Video(videoUri)
                    videos.add(video)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        })
    }

}