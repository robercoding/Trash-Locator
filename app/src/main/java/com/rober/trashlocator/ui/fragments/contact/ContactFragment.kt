package com.rober.trashlocator.ui.fragments.contact

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.rober.trashlocator.R
import com.rober.trashlocator.databinding.ContactFragmentBinding
import com.rober.trashlocator.ui.base.BaseFragment
import com.rober.trashlocator.ui.base.viewBinding


class ContactFragment : BaseFragment<ContactViewModel>(R.layout.contact_fragment) {
    override val viewModel: ContactViewModel by viewModels()
    private val binding: ContactFragmentBinding by viewBinding(ContactFragmentBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.contact_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun openTwitter() {
        val twitterIntent = buildTweetIntent()

        if (twitterIntent == null) {
            Toast.makeText(
                requireContext(),
                getString(R.string.twitter_not_found),
                Toast.LENGTH_LONG
            ).show()
            return
        }
        startActivity(twitterIntent)
    }

    private fun buildTweetIntent(): Intent? {
        val tweetIntent = Intent(Intent.ACTION_SEND)
        tweetIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Hi @robercoding,\nI want to notify you about the TrashLocator App!"
        )
        tweetIntent.type = "text/plain"

        val packManager: PackageManager = requireActivity().packageManager
        val resolvedInfoList =
            packManager.queryIntentActivities(tweetIntent, PackageManager.MATCH_DEFAULT_ONLY)

        for (resolveInfo in resolvedInfoList) {
            if (resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")) {
                tweetIntent.setClassName(
                    resolveInfo.activityInfo.packageName,
                    resolveInfo.activityInfo.name
                )
                break
            }
        }

        return tweetIntent
    }

    private fun openGmail() {
        val gmailIntent = buildGmailIntent()

        if (gmailIntent == null) {
            Log.d("SeeGmail", "Intent null!")
            Toast.makeText(requireContext(), getString(R.string.gmail_not_found), Toast.LENGTH_LONG)
                .show()
            return
        }
        startActivity(gmailIntent)
    }

    private fun buildGmailIntent(): Intent? {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "plain/text"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("robercoding@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Notify error Trash Locator")
            putExtra(
                Intent.EXTRA_TEXT, "Hi,\n" +
                        "I want to tell you something about the TrashLocator App!"
            )
        }
        if (intent.resolveActivity(requireContext().packageManager) == null) {
            Log.d("SeeGmail", "Resolve activity null!")
            return null
        }

        return intent
    }

    override fun setupListeners() {
        super.setupListeners()
        binding.icGmail.setOnClickListener {
            Toast.makeText(requireContext(), "Opening gmail", Toast.LENGTH_LONG).show()
            openGmail()
        }

        binding.icTwitter.setOnClickListener {
            Toast.makeText(requireContext(), "Opening twitter", Toast.LENGTH_LONG).show()
            openTwitter()
        }
    }
}