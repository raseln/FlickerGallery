package com.rasel.flickergallery.utils

import android.content.Intent
import android.net.Uri
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("imageUrl")
fun showImage(imageView: ImageView, url: String?) {
    url?.let {
        GlideApp.with(imageView)
            .load(it)
            .into(imageView)
    }
}

@BindingAdapter("setOnQueryTextListener")
fun setOnQueryTextListener(view: SearchView, search: (String?)-> Unit) {
    view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            search(query)
            return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            return false
        }
    })
}

@BindingAdapter("errorVisibility")
fun showError(textView: TextView, list: List<Any>?) {
    if (list.isNullOrEmpty()) {
        textView.visibility = View.VISIBLE
    } else {
        textView.visibility = View.GONE
    }
}

@BindingAdapter("android:htmlText")
fun setHtmlText(textView: TextView, htmlString: String?) {
    val sequence = Html.fromHtml(htmlString)
    val strBuilder = SpannableStringBuilder(sequence)
    val urls = strBuilder.getSpans(0, sequence.length, URLSpan::class.java)
    for (span in urls) {
        val start = strBuilder.getSpanStart(span)
        val end = strBuilder.getSpanEnd(span)
        val flag = strBuilder.getSpanFlags(span)
        val clickable = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(span.url))
                textView.context.startActivity(intent)
            }
        }
        strBuilder.setSpan(clickable, start, end, flag)
        strBuilder.removeSpan(span)
    }

    textView.text = strBuilder
    textView.movementMethod = LinkMovementMethod.getInstance()
}