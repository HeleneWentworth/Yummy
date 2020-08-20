package com.example.yummy

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.example.yummy.data.CategoriesGet
import com.example.yummy.databinding.FragmentHomeBinding
import com.example.yummy.data.RecipePost
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.fragment_recipes.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.NonCancellable.children
import java.text.FieldPosition


class HomeFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentHomeBinding
    private lateinit var db: FirebaseFirestore
    private var firstRun = true
    private var allowChange = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = Firebase.auth
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        auth.addAuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser == null) {
                Log.d("Authentication", "User is not logged in")
                findNavController().navigate(R.id.loginFragment)
            }
        }

        var adapter = GroupAdapter<GroupieViewHolder>()
        db = Firebase.firestore
        binding.RecipesView.adapter = adapter

        adapter.setOnItemClickListener { item, view ->
            val recipeItem = item as RecipeItem
            Log.d("Click", "Item ID: ${recipeItem.recipeItem.id}")
            val action = HomeFragmentDirections.actionHomeFragmentToDetailsFragment2(recipeItem.recipeItem.id)
            findNavController().navigate(action)
        }
        fun updateRecipeDisplay(){
            adapter.clear()
            db.collection("recipes").get()
                .addOnSuccessListener {
                    for (recipe in it){
                        val resultRecipeItem = recipe.toObject<RecipePost>()
                        resultRecipeItem.id = recipe.id
                        var search = binding.recipeSearch.text

                        if (resultRecipeItem.category.toLowerCase().contains(search.toString().toLowerCase()) || resultRecipeItem.name.toLowerCase().contains(search.toString().toLowerCase())){
                            Log.d("Search", "${resultRecipeItem.name}")
                            Log.d("Search", "${resultRecipeItem.category}")
                            adapter.add(RecipeItem(resultRecipeItem))
                        }
                    }
                    allowChange = true
                }

        }

        if (firstRun) {
            Log.d("firstrun", "$firstRun")
//            db.collection("recipes").get()
//                .addOnSuccessListener {
//                    for (recipe in it) {
//                        val resultRecipeItem = recipe.toObject<RecipePost>()
//                        resultRecipeItem.id = recipe.id
//                        Log.d("RecipeItem", "${resultRecipeItem.id}")
//                        adapter.add(RecipeItem(resultRecipeItem))
//                    }
//                }
//            firstRun = false
            if (allowChange){
                allowChange = false
                updateRecipeDisplay()
            }

        }

        binding.createRecipe.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_createFragment)
        }

        fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
            this.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    //afterTextChanged.invoke(s.toString())
                }

                override fun afterTextChanged(editable: Editable?) {
                    afterTextChanged.invoke(editable.toString())
                }
            })
        }

        binding.recipeSearch.afterTextChanged {

            Log.d("Search", "${binding.recipeSearch.text}")
//            binding.allRecipesView.removeAllViewsInLayout()

            if (allowChange){
                allowChange = false
                updateRecipeDisplay()
            }


        }


        return binding.root

    }

}

class RecipeItem(val recipeItem: RecipePost) : Item(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int){
        viewHolder.recipeViewName.text = recipeItem.name + " - " + recipeItem.category
        if(recipeItem.headerImageUrl != "" && recipeItem.headerImageUrl.isNotEmpty()){
            Picasso.get().load(recipeItem.headerImageUrl).fit().centerCrop().into(viewHolder.recipeViewImage)
        }
    }

    override fun getLayout(): Int = R.layout.fragment_recipes

}
