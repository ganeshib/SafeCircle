package com.example.safecircle

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment(R.layout.activity_home_fragment) {
    lateinit var inviteAdapter:InviteAdapter
    private val listContacts:ArrayList<ContactModel> = ArrayList()
    lateinit var mContext:Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext=context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val listMembers= listOf<MemberModel>(
            MemberModel("GN","ecity jsklaljdhadljhajlhasjlhfjlsdhfljsdhfjlshgjldsh","90%","200m"),
            MemberModel("GM","Kormangla ajhkajshjkashjksahfjdkhskjfhkjsdhfkjd","80%","1Km"),
            MemberModel("KG","HSR sljfsdljflsjfslkjfklsjfklsdjdfklsdjfklsjflds","70%","2Km"),
            MemberModel("GKK","JP jafhjskhfjkshdfjkshfjksdhfjkhdsjkfhsjkfhjksdhfj","60%","5Km"),
            )

        val adapter=MemberAdapter(listMembers)
        val recycler=requireView().findViewById<RecyclerView>(R.id.recyclerView)
        recycler.layoutManager=LinearLayoutManager(mContext)
        recycler.adapter=adapter


        inviteAdapter=InviteAdapter(listContacts)
        fetchDatabaseContacts()

        CoroutineScope(Dispatchers.IO).launch {
            insertDatabaseContact(fetchContact())
        }


        val inviteRecycler=requireView().findViewById<RecyclerView>(R.id.recyclerViewInvite)
        inviteRecycler.layoutManager=LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false)
        inviteRecycler.adapter=inviteAdapter

        val threeDots=requireView().findViewById<ImageView>(R.id.icon_three_dots)
        threeDots.setOnClickListener{
            SharedPref.putBoolean(PrefConstants.IS_USER_LOGGED_IN,false)
            FirebaseAuth.getInstance().signOut()
        }
    }

    private fun fetchDatabaseContacts(){
        val database=safeCircleDatabase.getDatabase(mContext)
        database.contactDao().getAllContacts().observe(viewLifecycleOwner){
            listContacts.clear()
            listContacts.addAll(it)
            inviteAdapter.notifyDataSetChanged()
        }
    }

    private suspend fun insertDatabaseContact(listContacts: ArrayList<ContactModel>) {
        val database = safeCircleDatabase.getDatabase(mContext)

        database.contactDao().insertAll(listContacts)
    }

    @SuppressLint("Range")
    private fun fetchContact(): ArrayList<ContactModel> {
        val listContacts:ArrayList<ContactModel> =ArrayList()

        val cr=requireActivity().contentResolver
        val cursor=cr.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null,null)
        if(cursor!=null && cursor.count>0){
            while(cursor!=null && cursor.moveToNext()){
                val id=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val hasPhoneNumber=cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                if(hasPhoneNumber>0){
                    var pCur=cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" =?",
                        arrayOf(id),
                        ""
                    )
                    if(pCur!=null && pCur.count>0){
                        while (pCur!=null && pCur.moveToNext()){
                            val phoneNum=pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            listContacts.add(ContactModel(name,phoneNum))
                        }
                        pCur.close()
                    }
                }
            }
            if(cursor!=null){
                cursor.close()
            }
        }
        return listContacts
    }
}