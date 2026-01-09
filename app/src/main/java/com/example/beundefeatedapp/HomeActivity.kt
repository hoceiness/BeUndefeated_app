package com.example.beundefeatedapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beundefeatedapp.data.MockMatchData
import com.example.beundefeatedapp.data.Match
import com.example.beundefeatedapp.data.Player
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.navigation.NavigationView

data class Suggestion(val nickname: String, val followers: String, val avatarResId: Int)
data class Comment(val nickname: String, val text: String)
data class Post(
    val nickname: String, 
    val content: String, 
    val timeAgo: String, 
    var likes: Int = 0, 
    var commentsCount: Int = 0,
    var isLiked: Boolean = false,
    val avatarResId: Int,
    val comments: MutableList<Comment> = mutableListOf()
)

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private var visibleCount = 4
    private lateinit var suggestionsAdapter: SuggestionsAdapter
    private val allSuggestions = listOf(
        Suggestion("Achraf", "12.5k", MockMatchData.playerAvatars[0]),
        Suggestion("Yassine", "8.9k", MockMatchData.playerAvatars[1]),
        Suggestion("Hakim", "25.1k", MockMatchData.playerAvatars[2]),
        Suggestion("Sofyan", "14.2k", MockMatchData.playerAvatars[3]),
        Suggestion("Nayef", "6.7k", MockMatchData.playerAvatars[4]),
        Suggestion("Noussair", "11.3k", MockMatchData.playerAvatars[5]),
        Suggestion("Selim", "9.4k", MockMatchData.playerAvatars[6]),
        Suggestion("Azzedine", "18.6k", MockMatchData.playerAvatars[7]),
        Suggestion("Munir", "7.2k", MockMatchData.playerAvatars[8]),
        Suggestion("Walid", "30.5k", MockMatchData.playerAvatars[0]),
        Suggestion("Brahim", "22.1k", MockMatchData.playerAvatars[1]),
        Suggestion("Amine", "5.4k", MockMatchData.playerAvatars[2])
    )

    private val postsList = mutableListOf<Post>()
    private lateinit var postsAdapter: PostsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        
        val mainView = findViewById<View>(R.id.main_content)
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
                insets
            }
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val email = intent.getStringExtra("EMAIL")
        val navEmail = navigationView.findViewById<TextView>(R.id.nav_email)
        navEmail?.text = email ?: "No Email"

        val logo = findViewById<ImageView>(R.id.logo)
        logo?.setOnClickListener {
            openDrawer()
        }

        val whatAreYouThinking = findViewById<TextView>(R.id.what_are_you_thinking)
        whatAreYouThinking?.setOnClickListener {
            val createPostDialog = CreatePostDialogFragment()
            createPostDialog.show(supportFragmentManager, "CreatePostDialogFragment")
        }

        val suggestionsRecyclerView = findViewById<RecyclerView>(R.id.suggestions_recycler_view)
        if (suggestionsRecyclerView != null) {
            suggestionsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            suggestionsAdapter = SuggestionsAdapter(allSuggestions) { visibleCount }
            suggestionsRecyclerView.adapter = suggestionsAdapter
        }

        findViewById<TextView>(R.id.load_more_suggestions)?.setOnClickListener {
            if (visibleCount < allSuggestions.size) {
                val oldSize = visibleCount
                visibleCount += 4
                if (visibleCount > allSuggestions.size) visibleCount = allSuggestions.size
                suggestionsAdapter.notifyItemRangeInserted(oldSize, visibleCount - oldSize)
                suggestionsRecyclerView?.smoothScrollToPosition(visibleCount - 1)
                if (visibleCount >= allSuggestions.size) it.visibility = View.GONE
            }
        }

        val postsRecyclerView = findViewById<RecyclerView>(R.id.posts_recycler_view)
        if (postsRecyclerView != null) {
            postsRecyclerView.layoutManager = LinearLayoutManager(this)
            postsAdapter = PostsAdapter(postsList) { post ->
                val commentBottomSheet = CommentBottomSheet(post) {
                    post.commentsCount++
                    postsAdapter.notifyDataSetChanged()
                }
                commentBottomSheet.show(supportFragmentManager, "CommentBottomSheet")
            }
            postsRecyclerView.adapter = postsAdapter
        }
        
        if (postsList.isEmpty()) {
            postsList.add(Post("Selma", "Let's play football tonight!", "2 hours ago", 5, 4, false, MockMatchData.playerAvatars[0]).apply {
                comments.add(Comment("Ali", "I'm in!"))
                comments.add(Comment("Omar", "Where exactly?"))
                comments.add(Comment("Brahim", "Count me too!"))
                comments.add(Comment("Selim", "What time?"))
            })
        }

        // --- NAV DRAWER ---
        navigationView.findViewById<TextView>(R.id.nav_settings)?.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        
        navigationView.findViewById<TextView>(R.id.nav_create_matchup)?.setOnClickListener {
            showFragment(CreateMatchupFragment())
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        navigationView.findViewById<TextView>(R.id.nav_create_panna)?.setOnClickListener {
            showFragment(CreatePannaMatchupFragment())
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        navigationView.findViewById<TextView>(R.id.nav_create_futsal)?.setOnClickListener {
            showFragment(CreateFutsalMatchupFragment())
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        
        navigationView.findViewById<TextView>(R.id.nav_about_us)?.setOnClickListener {
            showFragment(AboutUsFragment())
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        navigationView.findViewById<TextView>(R.id.nav_logout)?.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // --- BOTTOM NAV ---
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav_include)
        bottomNav?.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    supportFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    setHomeUIVisibility(true)
                    true
                }
                R.id.nav_history -> {
                    showFragment(MatchSectionFragment())
                    true
                }
                R.id.nav_deposit -> {
                    showFragment(DepositFragment())
                    true
                }
                R.id.nav_about -> {
                    showFragment(AboutUsFragment())
                    true
                }
                else -> false
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    if (supportFragmentManager.backStackEntryCount > 0) {
                        supportFragmentManager.popBackStack()
                        handler.post { 
                            if (supportFragmentManager.backStackEntryCount == 0) {
                                setHomeUIVisibility(true)
                            }
                        }
                    } else {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        })

        handleIntent(intent)
    }

    private val handler = android.os.Handler(android.os.Looper.getMainLooper())

    fun openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START)
    }

    private fun setHomeUIVisibility(isVisible: Boolean) {
        val visibility = if (isVisible) View.VISIBLE else View.GONE
        findViewById<View>(R.id.home_default_ui).visibility = visibility
        findViewById<View>(R.id.logo).visibility = visibility
        findViewById<View>(R.id.home_title).visibility = visibility
        findViewById<View>(R.id.mad_balance).visibility = visibility
        // Bottom nav is ALWAYS VISIBLE in Activity layouts now as requested
        findViewById<View>(R.id.bottom_nav_include).visibility = View.VISIBLE
        findViewById<View>(R.id.fragment_container).visibility = if (isVisible) View.GONE else View.VISIBLE
    }

    fun addPost(content: String) {
        val newPost = Post("Me", content, "Just now", 0, 0, false, R.drawable.player1)
        postsList.add(0, newPost)
        postsAdapter.notifyItemInserted(0)
        findViewById<RecyclerView>(R.id.posts_recycler_view)?.scrollToPosition(0)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val showFrag = intent?.getStringExtra("SHOW_FRAGMENT")
        when (showFrag) {
            "PROFILE" -> showFragment(ProfileFragment())
            "DEPOSIT" -> showFragment(DepositFragment())
            "WITHDRAWAL" -> showFragment(WithdrawFragment())
            "SQUAD" -> showFragment(SquadCreationFragment())
            "ABOUT" -> showFragment(AboutUsFragment())
            "GCU" -> showFragment(VerificationGCUFragment())
        }
    }

    fun showFragment(fragment: Fragment) {
        setHomeUIVisibility(false)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    // --- ADAPTERS ---
    class SuggestionsAdapter(private val suggestions: List<Suggestion>, private val getVisibleCount: () -> Int) : RecyclerView.Adapter<SuggestionsAdapter.SuggestionViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SuggestionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.suggestion_item, parent, false))
        override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
            val suggestion = suggestions[position]
            holder.nickname.text = suggestion.nickname
            holder.followers.text = suggestion.followers
            holder.avatar.setImageResource(suggestion.avatarResId)
            holder.followButton.setOnClickListener { Toast.makeText(it.context, "Following ${suggestion.nickname}", Toast.LENGTH_SHORT).show() }
        }
        override fun getItemCount(): Int = getVisibleCount()
        class SuggestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nickname: TextView = itemView.findViewById(R.id.suggestion_nickname)
            val followers: TextView = itemView.findViewById(R.id.suggestion_followers)
            val followButton: Button = itemView.findViewById(R.id.btn_follow_suggestion)
            val avatar: ImageView = itemView.findViewById(R.id.suggestion_avatar)
        }
    }

    class PostsAdapter(private val posts: List<Post>, private val onCommentClick: (Post) -> Unit) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false))
        override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
            val post = posts[position]
            holder.nickname.text = post.nickname
            holder.content.text = post.content
            holder.timeAgo.text = post.timeAgo
            holder.likeCount.text = "${post.likes} Likes"
            holder.commentCount.text = "${post.commentsCount} Comments"
            holder.avatar.setImageResource(post.avatarResId)
            updateLikeUI(holder, post)
            holder.likeButton.setOnClickListener {
                if (post.isLiked) { post.likes--; post.isLiked = false } else { post.likes++; post.isLiked = true }
                updateLikeUI(holder, post)
            }
            holder.commentButton.setOnClickListener { onCommentClick(post) }
        }
        private fun updateLikeUI(holder: PostViewHolder, post: Post) {
            holder.likeCount.text = "${post.likes} Likes"
            holder.likeButton.setTextColor(ContextCompat.getColor(holder.itemView.context, if (post.isLiked) android.R.color.holo_red_dark else android.R.color.darker_gray))
        }
        override fun getItemCount(): Int = posts.size
        class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nickname: TextView = itemView.findViewById(R.id.nickname)
            val content: TextView = itemView.findViewById(R.id.post_text)
            val timeAgo: TextView = itemView.findViewById(R.id.time_ago)
            val likeButton: Button = itemView.findViewById(R.id.like_button)
            val commentButton: Button = itemView.findViewById(R.id.comment_button)
            val likeCount: TextView = itemView.findViewById(R.id.like_count)
            val commentCount: TextView = itemView.findViewById(R.id.comment_count)
            val avatar: ImageView = itemView.findViewById(R.id.profile_image)
        }
    }
}

// --- FRAGMENTS ---

class ProfileFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_profile, container, false)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.logo)?.setOnClickListener { (activity as? HomeActivity)?.openDrawer() }
        
        setupEditClick(view, R.id.edit_nickname, R.id.nickname_value, "Nickname")
        setupEditClick(view, R.id.edit_gender, R.id.gender_value, "Gender")
        setupEditClick(view, R.id.edit_position, R.id.position_value, "Position")
        setupEditClick(view, R.id.edit_phone, R.id.phone_value, "Phone Number", InputType.TYPE_CLASS_PHONE)
    }

    private fun setupEditClick(view: View, penId: Int, valueId: Int, title: String, inputType: Int = InputType.TYPE_CLASS_TEXT) {
        val pen = view.findViewById<View>(penId)
        val valueText = view.findViewById<TextView>(valueId)
        pen?.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Edit $title")
            val input = EditText(requireContext())
            input.inputType = inputType
            input.setText(valueText.text)
            builder.setView(input)
            builder.setPositiveButton("Save") { _, _ -> valueText.text = input.text.toString() }
            builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            builder.show()
        }
    }
}

class DepositFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_deposit, container, false)
        val paymentSpinner = view.findViewById<Spinner>(R.id.payment_method_spinner)
        val amountInput = view.findViewById<EditText>(R.id.amount_input)
        val depositButton = view.findViewById<Button>(R.id.btn_deposit_now)
        val recentContainer = view.findViewById<LinearLayout>(R.id.recent_deposits_container)
        val balanceValue = view.findViewById<TextView>(R.id.balance_value)

        val methods = arrayOf("Credit Card", "Paypal", "Crypto", "Bank Transfer")
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, methods)
        adapter.setDropDownViewResource(R.layout.spinner_item)
        paymentSpinner.adapter = adapter
        // Programmatically set dropDownHeight to 144dp (roughly 3 items)
        try {
            val method = Spinner::class.java.getDeclaredMethod("setDropDownHeight", Int::class.javaPrimitiveType)
            method.isAccessible = true
            method.invoke(paymentSpinner, (144 * resources.displayMetrics.density).toInt())
        } catch (e: Exception) { /* fallback */ }

        depositButton.setOnClickListener {
            val amount = amountInput.text.toString()
            if (amount.isNotEmpty()) {
                val newItem = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    setPadding(0, 0, 0, 24)
                }
                newItem.addView(TextView(requireContext()).apply { text = paymentSpinner.selectedItem.toString(); setTextColor(ContextCompat.getColor(context, android.R.color.white)); textSize = 16f; setTypeface(null, android.graphics.Typeface.BOLD); layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f) })
                newItem.addView(TextView(requireContext()).apply { text = "+$amount MAD"; setTextColor(ContextCompat.getColor(context, android.R.color.white)); textSize = 16f; setTypeface(null, android.graphics.Typeface.BOLD) })
                recentContainer.addView(newItem, 0)
                val currentBalance = balanceValue.text.toString().replace(" MAD", "").replace(",", "").toDoubleOrNull() ?: 0.0
                balanceValue.text = String.format("%.1f", currentBalance + amount.toDouble())
                amountInput.setText("")
                Toast.makeText(requireContext(), "Deposit Successful!", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.logo)?.setOnClickListener { (activity as? HomeActivity)?.openDrawer() }
    }
}

class WithdrawFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_withdraw, container, false)
        val methodSpinner = view.findViewById<Spinner>(R.id.withdraw_method_spinner)
        val methods = arrayOf("Bank Transfer", "Paypal", "Cash Plus")
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, methods)
        adapter.setDropDownViewResource(R.layout.spinner_item)
        methodSpinner.adapter = adapter
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.logo)?.setOnClickListener { (activity as? HomeActivity)?.openDrawer() }
    }
}

class SquadCreationFragment : Fragment() {
    private var selectedPlayer: Player? = null
    private val squadMembers = mutableListOf<Player>()
    private var is7v7 = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_squad_creation, container, false)
        
        val slots = listOf(
            view.findViewById<View>(R.id.slot_1), view.findViewById<View>(R.id.slot_2),
            view.findViewById<View>(R.id.slot_3), view.findViewById<View>(R.id.slot_4),
            view.findViewById<View>(R.id.slot_5), view.findViewById<View>(R.id.slot_6),
            view.findViewById<View>(R.id.slot_7)
        )

        // Initial setup for Slot 1 (Leader/User)
        squadMembers.add(MockMatchData.playersPool[0])
        updateSlotUI(slots[0], MockMatchData.playersPool[0], "Leader")

        val searchInput = view.findViewById<EditText>(R.id.search_player_input)
        val searchResultsRv = view.findViewById<RecyclerView>(R.id.player_search_recycler_view)
        val addSelectedBtn = view.findViewById<Button>(R.id.btn_add_selected_player)
        val selectedLabel = view.findViewById<TextView>(R.id.selected_player_label)
        val squadSizeHint = view.findViewById<TextView>(R.id.squad_size_hint)
        val submitBtn = view.findViewById<Button>(R.id.btn_create_squad_submit)
        val typeGroup = view.findViewById<RadioGroup>(R.id.squad_type_group)

        val searchAdapter = PlayerSearchAdapter(mutableListOf(MockMatchData.playersPool[0])) { player ->
            selectedPlayer = player
            selectedLabel.text = "Selected Player: ${player.nickname}"
            selectedLabel.visibility = View.VISIBLE
            addSelectedBtn.isEnabled = true
        }
        // Initially show all players
        searchAdapter.updateList(MockMatchData.playersPool)

        searchResultsRv.layoutManager = LinearLayoutManager(context)
        searchResultsRv.adapter = searchAdapter

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    val filtered = MockMatchData.playersPool.filter { it.nickname.startsWith(query, ignoreCase = true) && !squadMembers.contains(it) }
                    searchAdapter.updateList(filtered)
                } else {
                    searchAdapter.updateList(MockMatchData.playersPool.filter { !squadMembers.contains(it) })
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        addSelectedBtn.setOnClickListener {
            selectedPlayer?.let { player ->
                if (squadMembers.size < (if (is7v7) 7 else 5)) {
                    squadMembers.add(player)
                    val nextIndex = squadMembers.size - 1
                    updateSlotUI(slots[nextIndex], player, "${nextIndex + 1}${getOrdinal(nextIndex + 1)}")
                    
                    // Reset selection
                    selectedPlayer = null
                    selectedLabel.visibility = View.GONE
                    addSelectedBtn.isEnabled = false
                    searchInput.setText("")
                    searchAdapter.updateList(MockMatchData.playersPool.filter { !squadMembers.contains(it) })
                    
                    val total = if (is7v7) 7 else 5
                    squadSizeHint.text = "Build your ultimate team of $total players. Add ${total - squadMembers.size} more."
                    submitBtn.text = "Create Squad (${squadMembers.size}/$total)"
                } else {
                    Toast.makeText(context, "Squad is full!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        typeGroup.setOnCheckedChangeListener { _, checkedId ->
            is7v7 = checkedId == R.id.radio_7v7
            val total = if (is7v7) 7 else { 5 }
            slots[5].visibility = if (is7v7) View.VISIBLE else View.GONE
            slots[6].visibility = if (is7v7) View.VISIBLE else View.GONE
            
            // If switched to 5v5 and had 6 or 7 members, remove them
            while (squadMembers.size > total) {
                val removed = squadMembers.removeAt(squadMembers.size - 1)
                clearSlotUI(slots[squadMembers.size])
            }

            squadSizeHint.text = "Build your ultimate team of $total players. Add ${total - squadMembers.size} more."
            submitBtn.text = "Create Squad (${squadMembers.size}/$total)"
        }

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.logo)?.setOnClickListener { (activity as? HomeActivity)?.openDrawer() }
    }

    private fun updateSlotUI(slotView: View, player: Player, role: String) {
        slotView.findViewById<TextView>(R.id.player_nickname).text = player.nickname
        slotView.findViewById<TextView>(R.id.player_status).text = if (player.isOnline) "online" else "offline"
        slotView.findViewById<ImageView>(R.id.status_dot).setImageResource(if (player.isOnline) R.drawable.status_online_dot else R.drawable.status_offline_dot)
        slotView.findViewById<TextView>(R.id.role_badge).text = role
        slotView.findViewById<ImageView>(R.id.player_avatar).setImageResource(player.avatarResId)
    }

    private fun clearSlotUI(slotView: View) {
        slotView.findViewById<TextView>(R.id.player_nickname).text = "Empty Slot"
        slotView.findViewById<TextView>(R.id.player_status).text = "offline"
        slotView.findViewById<ImageView>(R.id.status_dot).setImageResource(R.drawable.status_offline_dot)
        slotView.findViewById<TextView>(R.id.role_badge).text = "..."
        slotView.findViewById<ImageView>(R.id.player_avatar).setImageResource(R.drawable.logo)
    }

    private fun getOrdinal(i: Int) = when {
        i % 100 in 11..13 -> "th"
        i % 10 == 1 -> "st"
        i % 10 == 2 -> "nd"
        i % 10 == 3 -> "rd"
        else -> "th"
    }

    class PlayerSearchAdapter(private var players: MutableList<Player>, private val onAddClick: (Player) -> Unit) : RecyclerView.Adapter<PlayerSearchAdapter.ViewHolder>() {
        fun updateList(newList: List<Player>) { players.clear(); players.addAll(newList); notifyDataSetChanged() }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.player_search_item, parent, false))
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val player = players[position]
            holder.nickname.text = player.nickname
            holder.stats.text = "Win rate ${player.winRate} | Total matches ${player.totalMatches}"
            holder.status.text = if (player.isOnline) "● online" else "● offline"
            holder.status.setTextColor(ContextCompat.getColor(holder.itemView.context, if (player.isOnline) android.R.color.holo_green_dark else android.R.color.darker_gray))
            holder.avatar.setImageResource(player.avatarResId)
            holder.addBtn.setOnClickListener { onAddClick(player) }
        }
        override fun getItemCount() = players.size
        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val nickname: TextView = v.findViewById(R.id.search_result_nickname)
            val stats: TextView = v.findViewById(R.id.search_result_stats)
            val status: TextView = v.findViewById(R.id.search_result_status)
            val addBtn: Button = v.findViewById(R.id.btn_add_search_result)
            val avatar: ImageView = v.findViewById(R.id.search_result_avatar)
        }
    }
}

class AboutUsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_about_us, container, false)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.logo)?.setOnClickListener { (activity as? HomeActivity)?.openDrawer() }
    }
}

class VerificationGCUFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_gcu_verification, container, false)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.logo)?.setOnClickListener { (activity as? HomeActivity)?.openDrawer() }
    }
}

class CreatePannaMatchupFragment : MatchupBaseFragment(R.layout.fragment_create_panna_matchup)
class CreateFutsalMatchupFragment : MatchupBaseFragment(R.layout.fragment_create_futsal_matchup)

class MatchSectionFragment : Fragment() {
    private lateinit var adapter: MatchesAdapter
    private var filteredMatches = mutableListOf<Match>()
    private var displayedMatches = mutableListOf<Match>()
    private var visibleCount = 4
    
    private var selectedDay = "Today"
    private var selectedCategory = "All"
    private var selectedStatus = "All"
    private var selectedCity = "All"
    private var selectedTime = "AllDay"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_match_section, container, false)
        
        val recyclerView = view.findViewById<RecyclerView>(R.id.matches_recycler_view)
        adapter = MatchesAdapter(displayedMatches) { match ->
            val isHistoryMatch = match.day == "History"
            val isFullMatch = if (isHistoryMatch) true else match.currentPlayers >= match.maxPlayers
            (activity as? HomeActivity)?.showFragment(MatchDetailsFragment.newInstance(match.id, isHistoryMatch, isFullMatch))
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder): Boolean = false
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val matchToDelete = displayedMatches[position]
                MockMatchData.matches.remove(matchToDelete)
                filteredMatches.remove(matchToDelete)
                displayedMatches.removeAt(position)
                adapter.notifyItemRemoved(position)
                Toast.makeText(context, "Match deleted", Toast.LENGTH_SHORT).show()
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(recyclerView)

        setupFilters(view)
        
        view.findViewById<Button>(R.id.btn_load_more_matches).setOnClickListener {
            visibleCount += 4
            updateDisplayedList()
            if (visibleCount >= filteredMatches.size) it.visibility = View.GONE
        }

        applyFilters(view)

        return view
    }

    private fun setupFilters(view: View) {
        // Tab Buttons
        view.findViewById<Button>(R.id.tab_today).setOnClickListener { selectedDay = "Today"; updateFilterUI(view); applyFilters(view) }
        view.findViewById<Button>(R.id.tab_tomorrow).setOnClickListener { selectedDay = "Tomorrow"; updateFilterUI(view); applyFilters(view) }
        view.findViewById<Button>(R.id.tab_history).setOnClickListener { selectedDay = "History"; updateFilterUI(view); applyFilters(view) }

        // Category Buttons
        view.findViewById<Button>(R.id.filter_5v5).setOnClickListener { selectedCategory = if (selectedCategory == "5v5") "All" else "5v5"; updateFilterUI(view); applyFilters(view) }
        view.findViewById<Button>(R.id.filter_7v7).setOnClickListener { selectedCategory = if (selectedCategory == "7v7") "All" else "7v7"; updateFilterUI(view); applyFilters(view) }
        view.findViewById<Button>(R.id.filter_panna).setOnClickListener { selectedCategory = if (selectedCategory == "Panna") "All" else "Panna"; updateFilterUI(view); applyFilters(view) }
        view.findViewById<Button>(R.id.filter_futsal).setOnClickListener { selectedCategory = if (selectedCategory == "Futsal") "All" else "Futsal"; updateFilterUI(view); applyFilters(view) }

        // Status Buttons
        view.findViewById<Button>(R.id.filter_all).setOnClickListener { selectedStatus = "All"; updateFilterUI(view); applyFilters(view) }
        view.findViewById<Button>(R.id.filter_open).setOnClickListener { selectedStatus = if (selectedStatus == "Open") "All" else "Open"; updateFilterUI(view); applyFilters(view) }
        view.findViewById<Button>(R.id.filter_full).setOnClickListener { selectedStatus = if (selectedStatus == "Full") "All" else "Full"; updateFilterUI(view); applyFilters(view) }
        view.findViewById<Button>(R.id.filter_private).setOnClickListener { selectedStatus = if (selectedStatus == "Private") "All" else "Private"; updateFilterUI(view); applyFilters(view) }

        // Time Buttons
        view.findViewById<Button>(R.id.filter_allday).setOnClickListener { selectedTime = "AllDay"; updateFilterUI(view); applyFilters(view) }
        view.findViewById<Button>(R.id.filter_morning).setOnClickListener { selectedTime = if (selectedTime == "Morning") "AllDay" else "Morning"; updateFilterUI(view); applyFilters(view) }
        view.findViewById<Button>(R.id.filter_afternoon).setOnClickListener { selectedTime = if (selectedTime == "Afternoon") "AllDay" else "Afternoon"; updateFilterUI(view); applyFilters(view) }
        view.findViewById<Button>(R.id.filter_evening).setOnClickListener { selectedTime = if (selectedTime == "Evening") "AllDay" else "Evening"; updateFilterUI(view); applyFilters(view) }

        // City Spinner
        val citySpinner = view.findViewById<Spinner>(R.id.filter_city_spinner)
        val cityList = mutableListOf("All")
        cityList.addAll(MockMatchData.cities)
        val cityAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, cityList)
        cityAdapter.setDropDownViewResource(R.layout.spinner_item)
        citySpinner.adapter = cityAdapter
        citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                selectedCity = cityList[pos]
                applyFilters(view)
            }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }

        view.findViewById<Button>(R.id.btn_apply_filters).setOnClickListener { applyFilters(view) }
        updateFilterUI(view)
    }

    private fun updateFilterUI(view: View) {
        // Update Tabs using backgroundTintList for proper Material Button behavior
        val orange = ContextCompat.getColor(requireContext(), R.color.orange)
        val activeTint = ColorStateList.valueOf(orange)

        view.findViewById<Button>(R.id.tab_today).backgroundTintList = if (selectedDay == "Today") activeTint else ColorStateList.valueOf(ContextCompat.getColor(requireContext(), android.R.color.transparent))
        view.findViewById<Button>(R.id.tab_tomorrow).backgroundTintList = if (selectedDay == "Tomorrow") activeTint else ColorStateList.valueOf(ContextCompat.getColor(requireContext(), android.R.color.transparent))
        view.findViewById<Button>(R.id.tab_history).backgroundTintList = if (selectedDay == "History") activeTint else ColorStateList.valueOf(ContextCompat.getColor(requireContext(), android.R.color.transparent))

        // Update Categories
        view.findViewById<Button>(R.id.filter_5v5).setTextColor(if (selectedCategory == "5v5") orange else ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
        view.findViewById<Button>(R.id.filter_7v7).setTextColor(if (selectedCategory == "7v7") orange else ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
        view.findViewById<Button>(R.id.filter_panna).setTextColor(if (selectedCategory == "Panna") orange else ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
        view.findViewById<Button>(R.id.filter_futsal).setTextColor(if (selectedCategory == "Futsal") orange else ContextCompat.getColor(requireContext(), android.R.color.darker_gray))

        // Update Status
        view.findViewById<Button>(R.id.filter_all).setTextColor(if (selectedStatus == "All") orange else ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
        view.findViewById<Button>(R.id.filter_open).setTextColor(if (selectedStatus == "Open") orange else ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
        view.findViewById<Button>(R.id.filter_full).setTextColor(if (selectedStatus == "Full") orange else ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
        view.findViewById<Button>(R.id.filter_private).setTextColor(if (selectedStatus == "Private") orange else ContextCompat.getColor(requireContext(), android.R.color.darker_gray))

        // Update Time
        view.findViewById<Button>(R.id.filter_allday).setTextColor(if (selectedTime == "AllDay") orange else ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
        view.findViewById<Button>(R.id.filter_morning).setTextColor(if (selectedTime == "Morning") orange else ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
        view.findViewById<Button>(R.id.filter_afternoon).setTextColor(if (selectedTime == "Afternoon") orange else ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
        view.findViewById<Button>(R.id.filter_evening).setTextColor(if (selectedTime == "Evening") orange else ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
    }

    private fun applyFilters(view: View) {
        val matches = MockMatchData.matches.filter { match ->
            (match.day == selectedDay) &&
            (selectedCategory == "All" || match.type == selectedCategory) &&
            (selectedStatus == "All" || match.status == selectedStatus) &&
            (selectedCity == "All" || match.city == selectedCity) &&
            (selectedTime == "AllDay" || match.timeCategory == selectedTime)
        }
        
        filteredMatches.clear()
        filteredMatches.addAll(matches)
        visibleCount = 4
        updateDisplayedList()

        val loadMoreBtn = view.findViewById<Button>(R.id.btn_load_more_matches)
        loadMoreBtn.visibility = if (filteredMatches.size > visibleCount) View.VISIBLE else View.GONE

        view.findViewById<TextView>(R.id.results_title).text = "$selectedDay's available matches"
        view.findViewById<TextView>(R.id.results_count).text = "${matches.size} match found"
    }

    private fun updateDisplayedList() {
        displayedMatches.clear()
        displayedMatches.addAll(filteredMatches.take(visibleCount))
        adapter.notifyDataSetChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.logo)?.setOnClickListener { (activity as? HomeActivity)?.openDrawer() }
    }

    class MatchesAdapter(private val matches: List<Match>, private val onSeeDetails: (Match) -> Unit) : RecyclerView.Adapter<MatchesAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.match_item, parent, false))
        override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
            val match = matches[pos]
            val isHistory = match.day == "History"
            
            holder.category.text = "Category : ${match.type}"
            // Force full slots for history
            val current = if (isHistory) match.maxPlayers else match.currentPlayers
            holder.timeSlots.text = "● ${match.time}   ● $current/${match.maxPlayers} slots"
            
            holder.title.text = if (match.scoreA != null && match.scoreB != null) "Final Score: ${match.scoreA} - ${match.scoreB}" else "Game practice"
            holder.price.text = "${match.price} MAD\nPer Person"
            holder.location.text = "Field\n${match.field.split(" ")[0]}"
            holder.format.text = "${match.type}\nFormat"
            
            // Set team logos
            holder.logoA.setImageResource(match.teamALogoResId)
            holder.logoB.setImageResource(match.teamBLogoResId)

            // Disable join button for history OR if full
            val isFull = current >= match.maxPlayers
            holder.btnJoin.isEnabled = !isHistory && !isFull
            holder.btnJoin.alpha = if (isHistory || isFull) 0.5f else 1.0f
            
            holder.btnDetails.setOnClickListener { onSeeDetails(match) }
        }
        override fun getItemCount() = matches.size
        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val category: TextView = v.findViewById(R.id.match_category)
            val timeSlots: TextView = v.findViewById(R.id.match_time_slots)
            val title: TextView = v.findViewById(R.id.match_title)
            val price: TextView = v.findViewById(R.id.match_price)
            val location: TextView = v.findViewById(R.id.match_location)
            val format: TextView = v.findViewById(R.id.match_format)
            val btnJoin: Button = v.findViewById(R.id.btn_join_match)
            val btnDetails: Button = v.findViewById(R.id.btn_see_details)
            val logoA: ImageView = v.findViewById(R.id.team_a_logo)
            val logoB: ImageView = v.findViewById(R.id.team_b_logo)
        }
    }
}

class MatchDetailsFragment : Fragment() {
    private var isHistory: Boolean = false
    private var isFull: Boolean = false
    private val teamAPlayers = mutableListOf<Player>()
    private val teamBPlayers = mutableListOf<Player>()

    companion object {
        fun newInstance(matchId: Int, isHistory: Boolean, isFull: Boolean): MatchDetailsFragment {
            val fragment = MatchDetailsFragment()
            val args = Bundle()
            args.putInt("match_id", matchId)
            args.putBoolean("is_history", isHistory)
            args.putBoolean("is_full", isFull)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isHistory = arguments?.getBoolean("is_history") ?: false
        isFull = arguments?.getBoolean("is_full") ?: false
        
        // Mock initial players using the pool with real avatars
        val pool = MockMatchData.playersPool
        if (isHistory || isFull) {
            // Fill both teams completely
            val shuffled = pool.shuffled()
            repeat(5) { teamAPlayers.add(shuffled[it]) }
            repeat(5) { teamBPlayers.add(shuffled[it+5]) }
        } else {
            val shuffled = pool.shuffled()
            repeat(3) { teamAPlayers.add(shuffled[it]) }
            repeat(2) { teamBPlayers.add(shuffled[it+3]) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_match_details, container, false)
        updateUI(view)

        val btnJoinA = view.findViewById<Button>(R.id.btn_join_team_a)
        val btnJoinB = view.findViewById<Button>(R.id.btn_join_team_b)
        val btnLeave = view.findViewById<Button>(R.id.tab_leave)

        // Disable buttons if match is history or full
        if (isHistory || isFull) {
            btnJoinA.isEnabled = false
            btnJoinB.isEnabled = false
            btnJoinA.alpha = 0.5f
            btnJoinB.alpha = 0.5f
            btnLeave.visibility = View.GONE
        }

        btnJoinA.setOnClickListener {
            if (!teamAPlayers.any { it.nickname == "Me" } && !teamBPlayers.any { it.nickname == "Me" }) {
                if (teamAPlayers.size < 5) {
                    teamAPlayers.add(Player("Me", "100%", 1, true, R.drawable.player1))
                    updateUI(view)
                    Toast.makeText(context, "Joined Team A", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Team A is full", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Already in a team", Toast.LENGTH_SHORT).show()
            }
        }

        btnJoinB.setOnClickListener {
            if (!teamAPlayers.any { it.nickname == "Me" } && !teamBPlayers.any { it.nickname == "Me" }) {
                if (teamBPlayers.size < 5) {
                    teamBPlayers.add(Player("Me", "100%", 1, true, R.drawable.player1))
                    updateUI(view)
                    Toast.makeText(context, "Joined Team B", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Team B is full", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Already in a team", Toast.LENGTH_SHORT).show()
            }
        }

        btnLeave.setOnClickListener {
            val meA = teamAPlayers.find { it.nickname == "Me" }
            val meB = teamBPlayers.find { it.nickname == "Me" }
            if (meA != null) teamAPlayers.remove(meA)
            if (meB != null) teamBPlayers.remove(meB)
            updateUI(view)
            Toast.makeText(context, "Left Match", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun updateUI(view: View) {
        val aSlots = listOf(
            view.findViewById<View>(R.id.team_a_slot_1), view.findViewById<View>(R.id.team_a_slot_2),
            view.findViewById<View>(R.id.team_a_slot_3), view.findViewById<View>(R.id.team_a_slot_4),
            view.findViewById<View>(R.id.team_a_slot_5)
        )
        val bSlots = listOf(
            view.findViewById<View>(R.id.team_b_slot_1), view.findViewById<View>(R.id.team_b_slot_2),
            view.findViewById<View>(R.id.team_b_slot_3), view.findViewById<View>(R.id.team_b_slot_4),
            view.findViewById<View>(R.id.team_b_slot_5)
        )

        teamAPlayers.forEachIndexed { i, player -> if (i < aSlots.size) updateSlotUI(aSlots[i], player, "Player ${i+1}") }
        for (i in teamAPlayers.size until aSlots.size) { clearSlotUI(aSlots[i]) }

        teamBPlayers.forEachIndexed { i, player -> if (i < bSlots.size) updateSlotUI(bSlots[i], player, "Player ${i+1}") }
        for (i in teamBPlayers.size until bSlots.size) { clearSlotUI(bSlots[i]) }
    }

    private fun updateSlotUI(slotView: View, player: Player, role: String) {
        slotView.findViewById<TextView>(R.id.player_nickname).text = player.nickname
        slotView.findViewById<TextView>(R.id.player_status).text = if (player.isOnline) "online" else "offline"
        slotView.findViewById<ImageView>(R.id.status_dot).setImageResource(if (player.isOnline) R.drawable.status_online_dot else R.drawable.status_offline_dot)
        slotView.findViewById<TextView>(R.id.role_badge).text = role
        slotView.findViewById<ImageView>(R.id.player_avatar).setImageResource(player.avatarResId)
    }

    private fun clearSlotUI(slotView: View) {
        slotView.findViewById<TextView>(R.id.player_nickname).text = "Empty Slot"
        slotView.findViewById<TextView>(R.id.player_status).text = "offline"
        slotView.findViewById<ImageView>(R.id.status_dot).setImageResource(R.drawable.status_offline_dot)
        slotView.findViewById<TextView>(R.id.role_badge).text = "..."
        slotView.findViewById<ImageView>(R.id.player_avatar).setImageResource(R.drawable.logo)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.logo)?.setOnClickListener { (activity as? HomeActivity)?.openDrawer() }
    }
}

abstract class MatchupBaseFragment(private val layoutId: Int) : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(layoutId, container, false)
        val citySpinner = view.findViewById<Spinner>(R.id.city_spinner)
        if (citySpinner != null) {
            val cityAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, MockMatchData.cities)
            cityAdapter.setDropDownViewResource(R.layout.spinner_item)
            citySpinner.adapter = cityAdapter
        }
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.logo)?.setOnClickListener { (activity as? HomeActivity)?.openDrawer() }
        
        val citySpinner = view.findViewById<Spinner>(R.id.city_spinner)
        val fieldSpinner = view.findViewById<Spinner>(R.id.field_spinner)
        val hourSpinner = view.findViewById<Spinner>(R.id.hour_spinner)
        val teamSpinner = view.findViewById<Spinner>(R.id.team_spinner)
        val matchTypeGroup = view.findViewById<RadioGroup>(R.id.match_type_group)
        
        // Helper to force 3-item height programmatically
        fun set3ItemHeight(spinner: Spinner?) {
            spinner ?: return
            try {
                val method = Spinner::class.java.getDeclaredMethod("setDropDownHeight", Int::class.javaPrimitiveType)
                method.isAccessible = true
                method.invoke(spinner, (144 * resources.displayMetrics.density).toInt())
            } catch (e: Exception) { /* reflective call failed */ }
        }

        set3ItemHeight(citySpinner)
        set3ItemHeight(fieldSpinner)
        set3ItemHeight(hourSpinner)
        set3ItemHeight(teamSpinner)

        fun updateTeamAdapter(is7v7: Boolean) {
            val teams = if (is7v7) MockMatchData.teams7v7 else MockMatchData.teams5v5
            val teamAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, teams)
            teamAdapter.setDropDownViewResource(R.layout.spinner_item)
            teamSpinner?.adapter = teamAdapter
        }

        // Initial team adapter setup
        updateTeamAdapter(matchTypeGroup?.checkedRadioButtonId == R.id.radio_7v7)

        matchTypeGroup?.setOnCheckedChangeListener { _, checkedId ->
            updateTeamAdapter(checkedId == R.id.radio_7v7)
        }
        
        citySpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
                val city = MockMatchData.cities[position]
                val fields = MockMatchData.fieldsByCity[city] ?: emptyList()
                val fieldAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, fields)
                fieldAdapter.setDropDownViewResource(R.layout.spinner_item)
                fieldSpinner?.adapter = fieldAdapter
            }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }
        
        fieldSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
                val field = parent?.getItemAtPosition(position).toString()
                val hours = MockMatchData.getAvailableHours(field)
                val hourAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, hours)
                hourAdapter.setDropDownViewResource(R.layout.spinner_item)
                hourSpinner?.adapter = hourAdapter
            }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }

        // Common Pre-Teamup logic if present
        val preTeamupGroup = view.findViewById<RadioGroup>(R.id.pre_teamup_group)
        val chooseTeamContainer = view.findViewById<View>(R.id.choose_team_container)
        preTeamupGroup?.setOnCheckedChangeListener { _, checkedId ->
            chooseTeamContainer?.visibility = if (checkedId == R.id.radio_team) View.VISIBLE else View.GONE
        }
    }
}

class CreateMatchupFragment : MatchupBaseFragment(R.layout.fragment_create_matchup)

class CommentBottomSheet(private val post: Post, private val onCommentAdded: () -> Unit) : BottomSheetDialogFragment() {
    
    private lateinit var adapter: CommentsAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            if (bottomSheet != null) {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
                // Force full screen height
                bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            }
        }
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_comments, container, false)
        
        val recyclerView = view.findViewById<RecyclerView>(R.id.comments_recycler_view)
        val inputField = view.findViewById<EditText>(R.id.comment_input_field)
        val sendBtn = view.findViewById<View>(R.id.send_comment_button)
        val closeBtn = view.findViewById<View>(R.id.close_comments_button)

        adapter = CommentsAdapter(post.comments)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        sendBtn.setOnClickListener {
            val text = inputField.text.toString().trim()
            if (text.isNotEmpty()) {
                post.comments.add(Comment("Me", text))
                post.commentsCount++
                adapter.notifyItemInserted(post.comments.size - 1)
                recyclerView.scrollToPosition(post.comments.size - 1)
                inputField.setText("")
                onCommentAdded()
            }
        }

        closeBtn.setOnClickListener { dismiss() }

        return view
    }

    class CommentsAdapter(private val comments: List<Comment>) : RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CommentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false))
        override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
            holder.nickname.text = comments[position].nickname
            holder.text.text = comments[position].text
        }
        override fun getItemCount() = comments.size
        class CommentViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val nickname: TextView = v.findViewById(R.id.comment_nickname)
            val text: TextView = v.findViewById(R.id.comment_text)
        }
    }
}
