package `in`.rgpvnotes.buddies.activities

import `in`.rgpvnotes.buddies.R
import `in`.rgpvnotes.buddies.activities.auth.PhoneInputActivity
import `in`.rgpvnotes.buddies.adapters.ConversationsAdapter
import `in`.rgpvnotes.buddies.model.Conversation
import `in`.rgpvnotes.buddies.utils.FirestoreUtil
import `in`.rgpvnotes.buddies.viewModel.ConversationViewModel
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.app_bar_conversations.*
import kotlinx.android.synthetic.main.content_conversations.*

class ConversationsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,ConversationsAdapter.ConversationInterface {

    private lateinit var conversationViewModel: ConversationViewModel

    private var userList:ArrayList<Conversation> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversations)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser == null) {
            startActivity(Intent(this@ConversationsActivity, PhoneInputActivity::class.java))
            finish()
        }

        FirestoreUtil.getNewMessages(application)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)


        chat_recyclerView.setHasFixedSize(true)
        chat_recyclerView.layoutManager = LinearLayoutManager(this)

        val itemAnimator = DefaultItemAnimator()
        itemAnimator.addDuration = 500
        itemAnimator.removeDuration = 500
        chat_recyclerView.itemAnimator = itemAnimator


        val adapter = ConversationsAdapter(userList,this@ConversationsActivity,this)

        chat_recyclerView.adapter = adapter

        conversationViewModel = ViewModelProviders.of(this).get(ConversationViewModel::class.java)

        conversationViewModel.conversationList.observe(this,androidx.lifecycle.Observer {

            adapter.addItem(it)

        })


        add_chat.setOnClickListener {
            startActivity(Intent(this@ConversationsActivity,AllUsersActivity::class.java))
        }


    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.conversations, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_tools -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onClick(senderId: String) {

        val chatIntent = Intent(this@ConversationsActivity, ChatActivity::class.java)
        chatIntent.putExtra("sender_id", senderId)

        startActivity(chatIntent)


    }

}
