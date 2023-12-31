package com.example.message.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.message.R
import com.example.message.databinding.FragmentChatroomBinding
import com.example.message.ui.adapter.MessageAdapter
import com.example.message.util.Temp
import com.example.message.viewmodel.ChatRoomViewModel
import com.example.message.viewmodel.ChatRoomViewModelFactory
import java.math.BigInteger

class ChatRoomFragment : Fragment() {

    private val viewModel: ChatRoomViewModel by activityViewModels {
        ChatRoomViewModelFactory()
    }

    private val navigationArgs: ChatRoomFragmentArgs by navArgs()

    private lateinit var binding: FragmentChatroomBinding

    private lateinit var uid: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatroomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        binding.chatRoomFragment = this@ChatRoomFragment

        uid = navigationArgs.uid
        Log.d(this.toString(), uid)

        Temp.retrieverPublicKey = Pair(
            BigInteger(Temp.retriever?.publicKey?.first.toString()),
            BigInteger(Temp.retriever?.publicKey?.second.toString()),
        )
        Log.d(this.toString(), Temp.retrieverPublicKey.toString())
        val currentUserUid = Temp.currentUser?.uid ?: ""

        viewModel.getMessages(currentUserUid, uid)

        val adapter = MessageAdapter(uid = uid)

        binding.recyclerViewChats.adapter = adapter

        viewModel.messages.observe(this@ChatRoomFragment.viewLifecycleOwner) {
            it?.let {
                adapter.submitList(it)
            }
        }
    }

    fun onSendButton() {

        val text = binding.textInput.text.toString()
        if (viewModel.checkInput(text)) {
            Toast.makeText(
                context,
                getString(R.string.text_notification),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            viewModel.sendMessage(
                senderID = Temp.currentUser!!.uid,
                retrievedID = uid,
                text = text
            )
            Temp.messageTemp = text
            binding.textInput.setText("")
        }
    }
}