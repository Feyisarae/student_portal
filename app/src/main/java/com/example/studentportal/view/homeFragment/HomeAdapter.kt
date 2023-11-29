package com.example.studentportal.view.homeFragment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.studentportal.R
import com.example.studentportal.databinding.CardviewAdminActivityBinding
import com.example.studentportal.roomDataBase.CardModel

class HomeAdapter (
    private val context: Context,
    private var moduleCards: List<CardModel>,
    private var selectCard: CardModel?,
    private var listener: OnCardClick
) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {
    private var mSelectedCard: CardWrapper = CardWrapper()

    init {
        mSelectedCard.module = selectCard
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val layoutBinding: CardviewAdminActivityBinding =
            CardviewAdminActivityBinding.inflate(inflater, parent, false)
        return ViewHolder(layoutBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val module: CardModel = moduleCards[position]
        holder.bind(module, position)
    }

    override fun getItemCount(): Int {
        return moduleCards.size
    }

    private fun setSelectedCardStyle(binding: CardviewAdminActivityBinding) {
        binding.tvPageTitle.setTextColor(context.getColor(R.color.Gray_2))
        binding.contentConstraintLayout.setBackgroundColor(
            context.getColor(R.color.wine)
        )
        binding.portalCardview.setStrokeColor(
            ContextCompat.getColorStateList(context, R.color.Gray_6)
        )
    }

    private fun setUnselectedCardStyle(binding: CardviewAdminActivityBinding) {
        binding.tvPageTitle.setTextColor(context.getColor(R.color.Gray_6))
        binding.contentConstraintLayout.setBackgroundColor(context.getColor(R.color.white))
        binding.portalCardview.setStrokeColor(
            ContextCompat.getColorStateList(context, R.color.Gray_6)
        )
    }

    /**
     * Select or unselect card and update view
     */
    private fun selectOrUnselectCard(moduleCard: CardModel, position: Int) {
        mSelectedCard.module = if (mSelectedCard.module == moduleCard) null else moduleCard
        when {
            mSelectedCard.position == null -> notifyItemChanged(position)

            position == mSelectedCard.position -> {
                // a deselect took place, update only one card
                notifyItemChanged(position)
            }

            else -> {
                // a new item was selected, update current and previous selection
                notifyItemChanged(position)
                notifyItemChanged(mSelectedCard.position!!)
            }
        }
        mSelectedCard.position = position
    }

    private fun isSelected(moduleCard: CardModel, position: Int): Boolean {
        var result = moduleCard == mSelectedCard.module
        if (result) mSelectedCard.position = position
        return result
    }

    fun clearSelection() {
        mSelectedCard.reset()
    }

    inner class ViewHolder(private var binding: CardviewAdminActivityBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(moduleCard: CardModel, position: Int) {
            try {
                binding.memberImageView.setImageResource(moduleCard.image)
                binding.tvPageTitle.text = moduleCard.title

                if (isSelected(moduleCard, position)) {
                    setSelectedCardStyle(binding)
                } else {
                    setUnselectedCardStyle(binding)
                }

                binding.portalCardview.setOnClickListener {
                    selectOrUnselectCard(moduleCard, position)
                    listener.onCardClick(mSelectedCard.module)
                }
                binding.executePendingBindings()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    interface OnCardClick {
        fun onCardClick(selectedModule: CardModel?)
    }

    /**
     * This model will help us to update only specific cards instead of using notifySetChanged
     */
    inner

    class CardWrapper(
        var module: CardModel? = null,
        var position: Int? = null // default start value
    ) {
        fun reset() {
            val initPosition = position
            val initModule = module
            if (initPosition != null && initModule != null) {
                selectOrUnselectCard(initModule, initPosition)
            }
            module = null
            position = null
        }
    }

}