package com.example.blogblog.presentation.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blogblog.R
import com.example.blogblog.databinding.FragmentPostsBinding
import com.example.blogblog.presentation.adapters.PostsAdapter
import com.example.blogblog.presentation.models.Posts
import com.example.blogblog.presentation.ui.viewmodels.PostsViewModel
import com.example.blogblog.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostsFragment : Fragment(R.layout.fragment_posts) {

    private val viewModel: PostsViewModel by viewModels()
    private lateinit var postsAdapter: PostsAdapter
    private lateinit var dialogAddPostView: View
    private var _binding: FragmentPostsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        binding.fab.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            val inflater = this.layoutInflater
            dialogAddPostView = inflater.inflate(R.layout.fragment_new_post, null)
            builder.setView(dialogAddPostView)
            val dialog = builder.create()
            val body = dialogAddPostView.findViewById<EditText>(R.id.editMessageView).text
            val title = dialogAddPostView.findViewById<EditText>(R.id.editTitleView).text

            dialogAddPostView.findViewById<Button>(R.id.addPostBtn)?.setOnClickListener {
                val post = Posts(null, 1, body.toString(), title.toString(), 1)
                viewModel.addPost(post)
                dialog.cancel()
            }
            dialog.show()
        }
        postsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("post", it)
            }
            findNavController().navigate(R.id.action_postsFragment_to_commentsFragment, bundle)
        }

        viewModel.posts.observe(
            viewLifecycleOwner,
            Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressBar()
                        response.data?.let {
                            postsAdapter.differ.submitList(it.toMutableList())
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

        var job: Job? = null
        binding.etSearch.addTextChangedListener {
            job?.cancel()
            job = lifecycleScope.launch {
                delay(500L)
                it?.let {
                    if (it.isNotEmpty()) {
                        viewModel.filterPosts(it.toString().toInt())
                    }
                }
            }
        }
        viewModel.posts.observe(
            viewLifecycleOwner,
            Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressBar()
                        response.data?.let {
                            postsAdapter.differ.submitList(it.toMutableList())
                        }
                    }
                    is Resource.Error -> {
                        hideProgressBar()
                        response.message?.let { message ->
                            Log.e("posts", "An error occured: $message")
                        }
                    }
                    is Resource.Loading -> {
                        showProgressBar()
                    }
                }
            }
        )
    }

    private fun setupRecyclerView() {
        postsAdapter = PostsAdapter()
        binding.rvPosts.apply {
            adapter = postsAdapter
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
