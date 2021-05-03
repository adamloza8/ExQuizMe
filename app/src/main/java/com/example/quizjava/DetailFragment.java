package com.example.quizjava.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.quizjava.MVVM.QuizViewModel;
import com.example.quizjava.Model.QuizModel;
import com.example.quizjava.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class DetailFragment extends Fragment implements View.OnClickListener {


    TextView quizTitle, desc, level, question, lastscoretext;
    ImageView imageView;
    Button takeQuiz;

    NavController navController;
    int position = 0;
    QuizViewModel viewModel;
    String quizID, quizName;
    long totalQuestions = 0L;
    String currentUserId;


    public DetailFragment() {
        // Pusty publiczy konstruktor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate układu dla tego fragmentu
        return inflater.inflate(R.layout.detailfrag, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        navController = Navigation.findNavController(view);
        quizTitle = view.findViewById(R.id.detailquiztitle);
        desc = view.findViewById(R.id.detailquizdesc);
        level = view.findViewById(R.id.detailleveltext);
        question = view.findViewById(R.id.detailquestionstext);
        imageView = view.findViewById(R.id.detailimage);
        takeQuiz = view.findViewById(R.id.detailtakequizbutton);

        lastscoretext = view.findViewById(R.id.detaillastscoretext);

        position = DetailFragmentArgs.fromBundle(getArguments()).getPosition();

        takeQuiz.setOnClickListener(this);




    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        viewModel = new ViewModelProvider(getActivity()).get(QuizViewModel.class);
        viewModel.getLiveDatafromFireStore().observe(getViewLifecycleOwner(), new Observer<List<QuizModel>>() {
            @Override
            public void onChanged(List<QuizModel> quizModels) {
                quizTitle.setText(quizModels.get(position).getQuizname());
                desc.setText(quizModels.get(position).getDesc());
                question.setText(quizModels.get(position).getQuestion()+"");
                level.setText(quizModels.get(position).getLevel());

                Glide.with(getContext()).load(quizModels.get(position).getImage())     //Wyświetlenie obrazu dla kategorii
                        .placeholder(R.drawable.placeholder_image)
                        .centerCrop().into(imageView);


                // Wysyłanie dwóch wartości do fragmentu quiz
                quizID = quizModels.get(position).getQuizid();
                quizName = quizModels.get(position).getQuizname();
                totalQuestions = quizModels.get(position).getQuestion();


                LoadRECENTResult();

            }
        });
    }

    private void LoadRECENTResult() {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user  = firebaseAuth.getCurrentUser();                              //Pobranie poprzedniego wyniku
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();


        if (user!= null) {


            currentUserId = user.getUid();


            firebaseFirestore.collection("QuizList")
                    .document(quizID)
                    .collection("Results")
                    .document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {




                        DocumentSnapshot snapshot = task.getResult();

                        // W przypadku braku poprzednich wyników do wyświetlenia:
                        if (snapshot.exists() && snapshot!= null) {


                            Long answerofcorrect = snapshot.getLong("correct");
                            Long incorrectanswers = snapshot.getLong("wrong");
                            Long missedquestions = snapshot.getLong("missed");



                            Long total = answerofcorrect + incorrectanswers + missedquestions;



                            Long percent = (answerofcorrect*100)/total;

                            lastscoretext.setText(percent.toString() +"%");



                        }


                    } else {

                        lastscoretext.setText(task.getException().toString());
                    }


                }
            });

        }

    }

    @Override
    public void onClick(View v) {

        DetailFragmentDirections.ActionDetailFragmentToQuizFragment
                action = DetailFragmentDirections.actionDetailFragmentToQuizFragment();

        action.setQuizid(quizID);
        action.setQuizname(quizName);
        action.setTotalNumberOfQuestion(totalQuestions);
        navController.navigate(action);

    }
}