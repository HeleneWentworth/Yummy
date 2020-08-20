package com.example.yummy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.example.yummy.data.RecipePost
import com.example.yummy.databinding.FragmentDetailsBinding
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_details.view.*


class DetailsFragment : Fragment() {

    private lateinit var binding: FragmentDetailsBinding
    private lateinit var db: FirebaseFirestore
    private val args: DetailsFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_details, container, false)
//        binding.detailsName.setText(args.recipeId)
        db = Firebase.firestore


        db.collection("recipes").document(args.recipeId).get()
            .addOnSuccessListener {
                val item = it.toObject<RecipePost>()
                binding.detailName.text = item?.name
                binding.detailIngredients.text = item?.ingredients
                binding.detailCategory.text = item?.category
                binding.detailSteps.text = item?.steps
                if(item?.headerImageUrl != ""){
                    Picasso.get().load(item?.headerImageUrl).fit().centerCrop().into(binding.detailImage)
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Unable to find recipe", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_detailsFragment_to_homeFragment)
            }




        return binding.root
    }

}