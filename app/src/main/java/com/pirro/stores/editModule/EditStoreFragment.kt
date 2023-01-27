package com.pirro.stores.editModule

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.pirro.stores.R
import com.pirro.stores.common.entities.StoreEntity
import com.pirro.stores.common.utils.TypeError
import com.pirro.stores.mainModule.MainActivity
import com.pirro.stores.databinding.FragmentEditStoreBinding
import com.pirro.stores.editModule.viewModel.EditStoreViewModel
@Suppress("DEPRECATION", "NAME_SHADOWING")
class EditStoreFragment : Fragment() {

    private lateinit var mBinding: FragmentEditStoreBinding
    //MVVM
    private lateinit var mEditStoreViewModel: EditStoreViewModel

    private var mActivity: MainActivity? = null
    private var mIsEditMode: Boolean = false
    private lateinit var mStoreEntity: StoreEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mEditStoreViewModel = ViewModelProvider(requireActivity())[EditStoreViewModel::class.java]
    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle? ): View {
        mBinding = FragmentEditStoreBinding.inflate(inflater, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //MVVM
        setupViewModel()

        setupTextFields()
    }

    private fun setupViewModel() {
        mEditStoreViewModel.getStoreSelected().observe(viewLifecycleOwner) {
            mStoreEntity = it ?: StoreEntity()
            if (it != null) {
                mIsEditMode = true
                setUiSrotre(it)
            } else{
                mIsEditMode = false
            }
            setupActionBar()
        }

        mEditStoreViewModel.getResult().observe(viewLifecycleOwner) { result ->
            hideKeyboard()

            when(result){
                is StoreEntity -> {
                    val msgRes = if (result.id == 0L) R.string.edit_store_message_save_success
                                else R.string.edit_store_message_update_success
                    mEditStoreViewModel.setStoreSelected(mStoreEntity)

                    Snackbar.make(mBinding.root,
                        msgRes,
                        Snackbar.LENGTH_SHORT).show()

                    mActivity?.onBackPressed()
                }
            }
        }
        mEditStoreViewModel.getTypeError().observe(viewLifecycleOwner) {typeError ->
            if (typeError != TypeError.NONE) {
                val msgRes = when (typeError) {
                    TypeError.GET -> R.string.main_error_get
                    TypeError.INSERT -> R.string.main_error_insert
                    TypeError.UPDATE -> R.string.main_error_update
                    TypeError.DELETE -> R.string.main_error_delete
                    else -> R.string.main_error_unknown
                }
                Snackbar.make(mBinding.root, msgRes, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupActionBar() {
        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title = if (mIsEditMode) getString(R.string.edit_store_title_edit)
                                            else getString(R.string.edit_store_title_add)

        setHasOptionsMenu(true)
    }

    private fun setupTextFields() {
        with(mBinding){
            etName.addTextChangedListener { validateFields(tiName) }
            etPhone.addTextChangedListener { validateFields(tilPhone) }
            etPhotoUrl.addTextChangedListener {
                validateFields(mBinding.tilPhotoUrl)
                loadImage(it.toString().trim())
            }
        }
    }

    private fun loadImage(url: String){
        Glide.with(this)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(mBinding.imgPhoto)
    }

    private fun setUiSrotre(storeEntity: StoreEntity) {
        with(mBinding){
            etName.text = storeEntity.name.editable()
            etPhone.text = storeEntity.phone.editable()
            etWebside.text = storeEntity.website.editable()
            etPhotoUrl.text = storeEntity.photoUrl.editable()
        }
    }

    private fun String.editable(): Editable = Editable.Factory.getInstance().newEditable(this)


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Suppress("DEPRECATION")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                mActivity?.onBackPressed()
                true
            }
            R.id.action_save ->{
                if (validateFields(mBinding.tilPhotoUrl, mBinding.tilPhone, mBinding.tiName)) {

                    with(mStoreEntity) {
                        name = mBinding.etName.text.toString().trim()
                        phone = mBinding.etPhone.text.toString().trim()
                        website = mBinding.etWebside.text.toString().trim()
                        photoUrl = mBinding.etPhotoUrl.text.toString().trim()
                    }

                    if (mIsEditMode) mEditStoreViewModel.updateStore(mStoreEntity)
                    else mEditStoreViewModel.saveStore(mStoreEntity)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }

    private fun validateFields(vararg textField: TextInputLayout): Boolean {
        var isValid = true

        for (textField in textField){
            if (textField.editText?.text.toString().trim().isEmpty()){
                textField.error =getString(R.string.helper_requiered)
                isValid = false
            }else textField.error = null
        }
        if (!isValid) Snackbar.make(mBinding.root,
            R.string.edit_store_message_valid,
            Snackbar.LENGTH_SHORT).show()

        return isValid
    }


    private fun hideKeyboard(){
        val imm = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    override fun onDestroyView() {
        hideKeyboard()
        super.onDestroyView()
    }

    override fun onDestroy() {
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)
        mEditStoreViewModel.setResult(Any())
        mEditStoreViewModel.setTypeError(TypeError.NONE )

        setHasOptionsMenu(false)
        super.onDestroy()
    }
}