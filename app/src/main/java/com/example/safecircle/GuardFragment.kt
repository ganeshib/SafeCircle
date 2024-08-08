package com.example.safecircle

import InviteMailAdapter
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.safecircle.databinding.ActivityGuardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class GuardFragment : Fragment(R.layout.activity_guard),InviteMailAdapter.OnActionClick {
    lateinit var binding: ActivityGuardBinding
    lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext=context
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=ActivityGuardBinding.inflate(layoutInflater)
        val view = inflater.inflate(R.layout.activity_guard, container, false)


        binding.sendInvite.setOnClickListener {
            sendInvites()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getInvites()
    }
    private fun getInvites() {
        val firestore=Firebase.firestore
        firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.email.toString())
            .collection("invites").get().addOnCompleteListener{
                if(it.isSuccessful){
                    val list:ArrayList<String> = ArrayList()
                    for(item in it.result){
                        if(item.get("invite_status")==0L){
                            list.add(item.id)
                        }
                    }
                    val adapter=InviteMailAdapter(list,this)
                    binding.inviteRecycler.layoutManager=LinearLayoutManager(mContext)
                    binding.inviteRecycler.adapter=adapter

                }
            }
    }

    private fun sendInvites() {
        val mail=binding.inviteMail.text.toString()
        val firestore=Firebase.firestore
        val data= hashMapOf(
            "invite_status" to 0
        )

        val senderMail=FirebaseAuth.getInstance().currentUser?.email.toString()
        firestore.collection("users")
            .document(mail)
            .collection("invites")
            .document(senderMail).set(data)
            .addOnSuccessListener {

            }.addOnFailureListener {  }

    }

    override fun onAcceptClick(mail: String) {
        val firestore=Firebase.firestore
        val data= hashMapOf(
            "invite_status" to 1
        )

        val senderMail=FirebaseAuth.getInstance().currentUser?.email.toString()
        firestore.collection("users")
            .document(senderMail)
            .collection("invites")
            .document(mail).set(data)
            .addOnSuccessListener {

            }.addOnFailureListener {  }
    }

    override fun onDenyClick(mail: String) {
        val firestore=Firebase.firestore
        val data= hashMapOf(
            "invite_status" to -1
        )

        val senderMail=FirebaseAuth.getInstance().currentUser?.email.toString()
        firestore.collection("users")
            .document(senderMail)
            .collection("invites")
            .document(mail).set(data)
            .addOnSuccessListener {

            }.addOnFailureListener {  }
    }
}
