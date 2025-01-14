package io.legado.app.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.legado.app.R
import io.legado.app.base.BaseDialogFragment
import io.legado.app.databinding.DialogUpdateBinding
import io.legado.app.help.AppConfig
import io.legado.app.lib.theme.primaryColor
import io.legado.app.model.Download
import io.legado.app.utils.toastOnUi
import io.legado.app.utils.viewbindingdelegate.viewBinding
import io.legado.app.utils.windowSize
import io.noties.markwon.Markwon
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin

class UpdateDialog() : BaseDialogFragment() {

    constructor(newVersion: String, updateBody: String, url: String, name: String) : this() {
        arguments = Bundle().apply {
            putString("newVersion", newVersion)
            putString("updateBody", updateBody)
            putString("url", url)
            putString("name", name)
        }
    }

    val binding by viewBinding(DialogUpdateBinding::bind)

    override fun onStart() {
        super.onStart()
        val dm = requireActivity().windowSize
        dialog?.window?.setLayout(
            (dm.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_update, container)
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolBar.setBackgroundColor(primaryColor)
        binding.toolBar.title = arguments?.getString("newVersion")
        val updateBody = arguments?.getString("updateBody")
        if (updateBody == null) {
            toastOnUi("没有数据")
            dismiss()
            return
        }
        binding.textView.post {
            Markwon.builder(requireContext())
                .usePlugin(GlideImagesPlugin.create(requireContext()))
                .usePlugin(HtmlPlugin.create())
                .usePlugin(TablePlugin.create(requireContext()))
                .build()
                .setMarkdown(binding.textView, updateBody)
        }
        if (!AppConfig.isGooglePlay) {
            binding.toolBar.inflateMenu(R.menu.app_update)
            binding.toolBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_download -> {
                        val url = arguments?.getString("url")
                        val name = arguments?.getString("name")
                        if (url != null && name != null) {
                            Download.start(requireContext(), url, name)
                        }
                    }
                }
                return@setOnMenuItemClickListener true
            }
        }
    }

}