package com.example.pokemon

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pokemonkotlin.util.PokeUtils

class PokemonListAdapter(
    val context: Context, nameList: ArrayList<String>
) :
    RecyclerView.Adapter<PokemonListAdapter.ViewHolder>(), Filterable {

    lateinit var mListener: OnItemClickListener
    lateinit var mFilterChangedListener: OnFilterChangedListener
    var mSearchingText: String = ""
    var mTotalDataList: ArrayList<String>
    var mMatchedDataList: ArrayList<String>

    init {
        mTotalDataList = nameList
        mMatchedDataList = nameList
    }

    companion object {
        fun getHighlightedText(string: String, searchString: String, color: Int): SpannableString? {
            var spannableString = SpannableString(string)
            var targetStartIndex = string.indexOf(searchString)
            var targetEndIndex = targetStartIndex + searchString.length
            if (targetStartIndex < 0 || targetEndIndex < 0) {
                return null
            }
            spannableString.setSpan(
                ForegroundColorSpan(color),
                targetStartIndex,
                targetEndIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return spannableString
        }
    }

    interface OnItemClickListener {
        fun onItemClick(v: View, pokemonName: String)
    }

    interface OnFilterChangedListener {
        fun onFilterChanged(filterList: List<String>)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener;
    }

    fun setOnFilterChangedListener(listener: OnFilterChangedListener) {
        mFilterChangedListener = listener
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var searchName = constraint.toString().trim()
                if (searchName.isEmpty()) {
                    mMatchedDataList = mTotalDataList
                } else {
                    val filteredList = ArrayList<String>()
                    for (name in mTotalDataList) {
                        if (name.toLowerCase().contains(searchName.toLowerCase())) {
                            filteredList.add(name)
                        }
                    }
                    mMatchedDataList = filteredList
                }
                var filterResult = FilterResults()
                filterResult.values = mMatchedDataList
                return filterResult
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                mMatchedDataList = results?.values as ArrayList<String>
                notifyDataSetChanged()
                mFilterChangedListener?.onFilterChanged(mMatchedDataList)
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view = inflater.inflate(R.layout.recyclerview_item, parent, false)
        var vh = ViewHolder(view)
        return vh
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mName = itemView.findViewById(R.id.text_item) as TextView

        init {
            itemView.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    var pos = adapterPosition
                    if (pos != RecyclerView.NO_POSITION) {
                        var name = mMatchedDataList.get(pos)
                        if (!name.isNullOrEmpty()) {
                            v?.let { mListener.onItemClick(it, name) }
                        }

                    }
                }
            })
        }
    }

    override fun getItemCount(): Int {
        return mMatchedDataList.size
    }

    fun getFilterDataList(): List<String> {
        return mMatchedDataList
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var name = mMatchedDataList.get(position)
        if (!name.isEmpty()) {
            if (!mSearchingText.isNullOrEmpty() && mMatchedDataList.size > 0) {
                var highlitedText = PokeUtils.getHighlightedText(
                    name.toLowerCase(),
                    mSearchingText.toLowerCase(),
                    context.resources.getColor(R.color.app_theme_color)
                )
                if (highlitedText != null) {
                    holder.mName.setText(highlitedText)
                } else {
                    holder.mName.setText(name)
                }
            } else {
                holder.mName.setText(name)
            }
        }
    }

}