package com.example.studentportal.view.studentList

import android.content.Context
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.studentportal.R
import com.example.studentportal.databinding.CardviewStudentListBinding
import com.example.studentportal.roomDataBase.Constant
import com.example.studentportal.roomDataBase.student.Student
import com.squareup.picasso.Picasso
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

val SEARCH_COMPARATOR = object : DiffUtil.ItemCallback<Student>() {
    override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean =
        // User ID serves as unique ID
        oldItem.studentId == newItem.studentId

    override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean =
        // Compare full contents (note: Java users should call .equals())
        oldItem == newItem
}

class StudentListAdapter(
    val mContext: Context,
    selectedStudent: Student?,
    val cachedImageFileNames: List<String>,
    val cardSelectListener: CardSelectListener,
) : PagingDataAdapter<Student, StudentListAdapter.ViewHolder>(SEARCH_COMPARATOR) {
    private var mSelectedCard: CardWrapper = CardWrapper()

    init {
        mSelectedCard.student = selectedStudent
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val member: Student? = getItem(position)
        holder.bind(member, position)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val layoutBinding =
            CardviewStudentListBinding.inflate(
                inflater,
                parent,
                false
            )
        return ViewHolder(layoutBinding)
    }

    // With this, every event location has a specific view-type
    override fun getItemViewType(position: Int): Int {
        return position
    }

    private fun setSelectedCardStyle(binding: CardviewStudentListBinding) {

        // set text color of all textViews
        for (i in 0 until binding.contentConstraintLayout.childCount) {
            val v: View = binding.contentConstraintLayout.getChildAt(i)
            if (v is TextView) {
                v.setTextColor(mContext.getColor(R.color.white))
            }
        }

        // set card background and stroke color
        binding.studentListCardView.setBackgroundColor(
            mContext.getColor(R.color.wine)
        )
        binding.studentListCardView.setStrokeColor(
            ContextCompat.getColorStateList(
                mContext,
                R.color.Gray_6
            )
        )
    }

    private fun setUnselectedCardStyle(binding: CardviewStudentListBinding) {
        // set text color of all textViews
        for (i in 0 until binding.contentConstraintLayout.childCount) {
            val v: View = binding.contentConstraintLayout.getChildAt(i)
            if (v is TextView) {
                v.setTextColor(mContext.getColor(R.color.Gray_6))
            }
        }

        // set card background and stroke color
        binding.studentListCardView.setBackgroundColor(
            mContext.getColor(R.color.white)
        )
        binding.studentListCardView.setStrokeColor(
            ContextCompat.getColorStateList(
                mContext,
                R.color.Gray_6
            )
        )
    }

    /**
     * Select or unselect card and update view
     */
    private fun selectOrUnselectCard(student: Student, position: Int) {
        mSelectedCard.student =
            if (mSelectedCard.student == student) null else student
        when {
            mSelectedCard.position == null -> {
                notifyItemChanged(position)
            }

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

    /**
     * Check if a member is selected
     */
    private fun isSelected(student: Student): Boolean {
        return student == mSelectedCard.student
    }

    fun clearSelection() {
        mSelectedCard.reset()
    }

    inner class ViewHolder(val binding: CardviewStudentListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(student: Student?, position: Int) {
            try {
                if (student == null) {
                    return
                }

                val name =
                    "${student.firstName} ${student.lastName}".trim()

                binding.tvName.text = name
                binding.tvDepartment.text = student.department
                binding.tvPageTitle.text = student.studentId

                if (student.blacklistStatus == 1) {
                    binding.warningImageview.visibility = View.VISIBLE
                    binding.tvBlacklist.visibility = View.VISIBLE
                } else {
                    binding.tvBlacklist.visibility = View.GONE
                    binding.warningImageview.visibility = View.GONE
                }
                loadCapturedImage(student.studentId)

                binding.studentListCardView.setOnClickListener {
                    selectOrUnselectCard(student, position)
                    cardSelectListener.onCardSelect(mSelectedCard.student)
                }

                if (isSelected(student)) {
                    mSelectedCard.position = position
                    setSelectedCardStyle(binding)
                } else {
                    setUnselectedCardStyle(binding)
                }

                binding.executePendingBindings()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun loadCapturedImage(studentId: String) {
            val studentDirectory =
                File(
                    Objects.requireNonNull<File>(
                        mContext.applicationContext.getExternalFilesDir(
                            Environment.DIRECTORY_PICTURES
                        )
                    ).absoluteFile,
                    Constant.Student_Directory
                )
            val currentDate = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val formattedDate = dateFormat.format(currentDate)
            val studentImage = File(
                studentDirectory.absoluteFile,
                "${studentId}_$formattedDate.jpg"

            )
            if (studentImage.exists()) {
                Picasso.get().load(studentImage)
                    .into(binding.studentImageview)
            }
        }

    }

    /**
     * This model will help us to update only specific cards instead of using notifySetChanged
     */
    inner class CardWrapper(
        var student: Student? = null,
        var position: Int? = null, // default start value
    ) {
        fun reset() {
            student = null
            position = null
        }
    }

    interface CardSelectListener {
        fun onCardSelect(selectedStudent: Student?)
    }
}