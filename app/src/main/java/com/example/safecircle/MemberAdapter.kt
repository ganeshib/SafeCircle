package com.example.safecircle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MemberAdapter(private val listMembers: List<MemberModel>) : RecyclerView.Adapter<MemberAdapter.ViewHolder>() {
    class ViewHolder(item: View):RecyclerView.ViewHolder(item) {
        val imageUser=item.findViewById<ImageView>(R.id.img_user)
        val name=item.findViewById<TextView>(R.id.user_name)
        val address=item.findViewById<TextView>(R.id.address)
        val batteryInfo=item.findViewById<TextView>(R.id.battery_percent)
        val distVal=item.findViewById<TextView>(R.id.distance_value)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        val item=inflater.inflate(R.layout.item_member,parent,false)
        return ViewHolder(item)
    }

    override fun getItemCount(): Int {
        return listMembers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=listMembers[position]
        holder.name.text=item.name
        holder.address.text=item.address
        holder.batteryInfo.text=item.batteryInfo
        holder.distVal.text=item.distance
    }
}