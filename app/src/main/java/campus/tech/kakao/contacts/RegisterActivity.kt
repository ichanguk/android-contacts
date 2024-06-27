package campus.tech.kakao.contacts

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RegisterActivity : AppCompatActivity() {
    lateinit var registerBtnLayout: FrameLayout
    lateinit var contactRecyclerView: RecyclerView
    lateinit var howToRegisterTextView: TextView
    private lateinit var startActivityLauncher: ActivityResultLauncher<Intent>
    private val contactList = mutableListOf<Contact>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initViews()
        setOnClickListeners()
        setContactRecyclerView()
        setStartActivityLauncher()
    }

    /**
     * 사용할 view들을 초기화하는 함수
     *
     * - `registerBtnLayout` : 연락처 등록 버튼을 나타내는 FrameLayout
     * - `contactRecyclerView` : 연락처 목록을 나타내는 RecyclerView
     * - `howToRegisterTextView` : 연락처 등록 방법 메시지를 나타내는 TextView
     */
    private fun initViews() {
        registerBtnLayout = findViewById(R.id.register_btn_layout)
        contactRecyclerView = findViewById(R.id.contact_recycler_view)
        howToRegisterTextView = findViewById(R.id.how_to_register_text_view)
    }

    /**
     * 사용할 클릭 리스너들을 설정하는 함수
     */
    private fun setOnClickListeners() {
        setOnClickListenerOfRegisterBtnLayout()
    }

    /**
     * 연락처 추가 버튼에 대한 클릭 리스너를 설정하는 함수
     *
     * 클릭 시 MainActivity로 넘어가고 Contact 객체를 결과로 받는 것을 기다림.
     */
    private fun setOnClickListenerOfRegisterBtnLayout() {
        registerBtnLayout.setOnClickListener {
            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
            startActivityLauncher.launch(intent)
        }
    }

    /**
     * 연락처 리사이클러뷰를 설정하는 함수
     *
     */
    private fun setContactRecyclerView() {
        contactRecyclerView.adapter =
            ContactRecyclerViewAdapter(contactList, LayoutInflater.from(this))
        contactRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    /**
     * startActivityLauncher를 설정하는 함수
     *
     * result로 contact 객체를 받아와 list에 추가.
     * 하나 이상의 contact 객체가 들어오면 등록 안내 textview의 visibility를 gone으로 설정.
     */
    private fun setStartActivityLauncher() {
        startActivityLauncher = registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val contact: Contact? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.data?.getParcelableExtra("CONTACT_RESULT", Contact::class.java)
                } else {
                    result.data?.getParcelableExtra("CONTACT_RESULT")
                }
                contact?.let {
                    contactList.add(it)
                    howToRegisterTextView.visibility = View.GONE
                    contactRecyclerView.adapter?.notifyDataSetChanged()

                }
            }
        }
    }

    class ContactRecyclerViewAdapter(
        var contactList: MutableList<Contact>,
        var inflater: LayoutInflater
    ) : RecyclerView.Adapter<ContactRecyclerViewAdapter.ViewHolder>() {
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val lastNameTextView: TextView
            val nameTextView: TextView

            init {
                lastNameTextView = itemView.findViewById(R.id.last_name_text_view)
                nameTextView = itemView.findViewById(R.id.name_text_view)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = inflater.inflate(R.layout.contact_item, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return contactList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.lastNameTextView.text = contactList.get(position).name.get(0).toString()
            holder.nameTextView.text = contactList.get(position).name
        }
    }

}