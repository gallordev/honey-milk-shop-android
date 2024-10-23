package com.honeymilk.shop.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.core.view.isGone
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputLayout
import com.honeymilk.shop.R
import com.honeymilk.shop.model.Presentation
import com.honeymilk.shop.utils.getText

class PresentationsGroup : LinearLayout {

    private lateinit var viewsMap: Map<String, Map<String, View>>
    private var isUpdatingChildren = false

    companion object {
        private const val CHECK_BOX_KEY = "checkBox"
        private const val INPUT_TEXT_KEY = "inputText"
    }

    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        inflate(context, R.layout.layout_presentations_group, this)
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.PresentationsGroup)
            val itemsArrayId =
                typedArray.getResourceId(R.styleable.PresentationsGroup_checkboxItems, 0)
            if (itemsArrayId != 0) {
                setFormItems(context.resources.getStringArray(itemsArrayId))
            }
            typedArray.recycle()
        } ?: return
    }
    private fun setParentState(
        checkBoxParent: MaterialCheckBox,
        childrenViews: Map<String, Map<String, View>>,
        parentOnCheckedStateChangedListener: MaterialCheckBox.OnCheckedStateChangedListener
    ) {
        val childrenCheckBoxes: List<MaterialCheckBox> = childrenViews.map {
            it.value[CHECK_BOX_KEY] as MaterialCheckBox
        }
        val checkedCount = childrenCheckBoxes
            .stream()
            .filter { obj: CheckBox -> obj.isChecked }
            .count()
            .toInt()
        val allChecked = checkedCount == childrenCheckBoxes.size
        val noneChecked = checkedCount == 0
        checkBoxParent.removeOnCheckedStateChangedListener(parentOnCheckedStateChangedListener)
        if (allChecked) {
            checkBoxParent.isChecked = true
        } else if (noneChecked) {
            checkBoxParent.isChecked = false
        } else {
            checkBoxParent.checkedState = MaterialCheckBox.STATE_INDETERMINATE
        }
        checkBoxParent.addOnCheckedStateChangedListener(parentOnCheckedStateChangedListener)
    }

    fun setFormItems(items: Array<String>) {
        val checkboxContainer = findViewById<LinearLayout>(R.id.checkbox_container)

        val tempMap = mutableMapOf<String, Map<String, View>>()

        items.forEach {
            val checkBox = MaterialCheckBox(context).apply { text = it }
            val inputText = LayoutInflater.from(context)
                .inflate(R.layout.layout_presentation_form_item, checkboxContainer, false)
            tempMap[it] = mapOf(CHECK_BOX_KEY to checkBox, INPUT_TEXT_KEY to inputText)
        }

        viewsMap = tempMap

        viewsMap.forEach { view ->
            checkboxContainer.addView(view.value[CHECK_BOX_KEY])
            checkboxContainer.addView(view.value[INPUT_TEXT_KEY])
        }

        val checkBoxParent = findViewById<CheckBox>(R.id.checkbox_parent)

        val parentOnCheckedStateChangedListener =
            MaterialCheckBox.OnCheckedStateChangedListener { checkBox: MaterialCheckBox, state: Int ->
                val isChecked = checkBox.isChecked
                if (state != MaterialCheckBox.STATE_INDETERMINATE) {
                    isUpdatingChildren = true
                    for (view in viewsMap) {
                        with(view) {
                            (value[CHECK_BOX_KEY] as MaterialCheckBox).isChecked = isChecked
                            (value[INPUT_TEXT_KEY] as View).isGone = !isChecked
                        }
                    }
                    isUpdatingChildren = false
                }
            }

        val childOnCheckedStateChangedListener =
            MaterialCheckBox.OnCheckedStateChangedListener { _: MaterialCheckBox?, _: Int ->
                viewsMap.forEach {
                    val isChecked = (it.value[CHECK_BOX_KEY] as MaterialCheckBox).isChecked
                    (it.value[INPUT_TEXT_KEY] as View).isGone = !isChecked
                }
                if (!isUpdatingChildren) {
                    setParentState(
                        checkBoxParent as MaterialCheckBox,
                        viewsMap,
                        parentOnCheckedStateChangedListener
                    )
                }
            }

        for (child in viewsMap) {
            (child.value[CHECK_BOX_KEY] as MaterialCheckBox).addOnCheckedStateChangedListener(
                childOnCheckedStateChangedListener
            )
        }

        setParentState(
            checkBoxParent as MaterialCheckBox,
            viewsMap,
            parentOnCheckedStateChangedListener
        )
    }

    fun setPresentationsData(data: List<Presentation>) {
        if (!(this::viewsMap.isInitialized) || viewsMap.isEmpty()) return
        viewsMap.forEach { view ->
            data.find { presentation ->
                presentation.name == view.key
            }?.let {
                val checkBox = view.value[CHECK_BOX_KEY] as MaterialCheckBox
                val inputText = view.value[INPUT_TEXT_KEY] as TextInputLayout
                checkBox.isChecked = true
                inputText.editText?.setText(it.price.toString())
            }
        }
    }

    fun getPresentationsData(): List<Presentation> {
        val data = mutableListOf<Presentation>()
        viewsMap.forEach { view ->
            val checkBox = view.value[CHECK_BOX_KEY] as MaterialCheckBox
            val price = (view.value[INPUT_TEXT_KEY] as TextInputLayout).getText().toFloatOrNull() ?: 0f
            if (checkBox.isChecked && price != 0f) {
                data.add(Presentation(view.key, price))
            }
        }
        return data
    }

}