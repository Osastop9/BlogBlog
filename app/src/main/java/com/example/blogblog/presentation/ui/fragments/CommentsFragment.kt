package com.example.blogblog.presentation.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blogblog.R
import com.example.blogblog.databinding.FragmentCommentsBinding
import com.example.blogblog.presentation.adapters.CommentsAdapter
import com.example.blogblog.presentation.models.Comments
import com.example.blogblog.presentation.models.Posts
import com.example.blogblog.presentation.ui.viewmodels.CommentsViewModel
import com.example.blogblog.util.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommentsFragment : Fragment(R.layout.fragment_comments) {

    private val viewModel: CommentsViewModel by viewModels()
    private lateinit var commentsAdapter: CommentsAdapter
    private lateinit var dialogAddPostView: View
    private var _binding: FragmentCommentsBinding? = null
    private val binding get() = _binding!!
    private val args: CommentsFragmentArgs by navArgs()
    lateinit var post: Posts

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        bindUI()

        binding.addCommentBtn.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            val inflater = this.layoutInflater
            dialogAddPostView = inflater.inflate(R.layout.fragment_new_comment, null)
            builder.setView(dialogAddPostView)
            val dialog = builder.create()
            post = args.post
            val postID = post.id
            val name = dialogAddPostView.findViewById<EditText>(R.id.editIDview).text
            val body = dialogAddPostView.findViewById<EditText>(R.id.editMessageView).text
            val email = dialogAddPostView.findViewById<EditText>(R.id.editTitleView).text

            dialogAddPostView.findViewById<Button>(R.id.addNewCommentBtn)?.setOnClickListener {
                val comment = Comments(null, 0, postID, name.toString(), email.toString(), body.toString())
                viewModel.addComments(postID, comment)
                dialog.cancel()
            }
            dialog.show()
        }

        viewModel.comments.observe(
            viewLifecycleOwner,
            Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressBar()
                        response.data?.let {
                            commentsAdapter.differ.submitList(it.toMutableList())
                        }
                    }
                    is Resource.Error -> {
                        hideProgressBar()
                        response.message?.let { message ->
                            Log.e("Post Fragment", "An error occured: $message")
                        }
                    }
                    is Resource.Loading -> {
                        showProgressBar()
                    }
                }
            }
        )
    }

    private fun bindUI() {

        val post = args.post
        viewModel.loadComments(post.id)
        binding.postDetailTitle.text = post.title
        binding.postDetailDesc.text = post.body
    }

    private fun setupRecyclerView() {
        commentsAdapter = CommentsAdapter()
        binding.rvComment.apply {
            adapter = commentsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
    }
    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
