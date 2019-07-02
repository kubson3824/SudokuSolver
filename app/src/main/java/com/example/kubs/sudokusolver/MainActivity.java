package com.example.kubs.sudokusolver;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.example.kubs.sudokusolver.ui.main.BoardFragment;
import com.example.kubs.sudokusolver.ui.main.SolvedFragment;
import com.example.kubs.sudokusolver.ui.main.PuzzleHistoryFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnFragmentInteractionListener{

    public static final int BOARD_FRAGMENT = 0;
    public static final int SOLVED_FRAGMENT= 1;
    public static final int USER_FRAGMENT = 2;
    public static final int FRAGMENTS = 3;

    private FragmentPagerAdapter fragmentPagerAdapter;
    private ViewPager viewPager;

    private List<Fragment> fragments = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        fragments.add(BOARD_FRAGMENT, new BoardFragment());
        fragments.add(SOLVED_FRAGMENT, new SolvedFragment());
        fragments.add(USER_FRAGMENT, new PuzzleHistoryFragment());

        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return FRAGMENTS;
            }

            @Override
            public Fragment getItem(int i) {
                return fragments.get(i);
            }

            @Override
            public CharSequence getPageTitle(final int position) {
                switch (position) {
                    case BOARD_FRAGMENT:
                        return getString(R.string.board_text);
                    case SOLVED_FRAGMENT:
                        return getString(R.string.solution_text);
                    case USER_FRAGMENT:
                        return getString(R.string.user_text);
                    default:
                        return null;
                }
            }
        };

        viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(fragmentPagerAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void setValues(Integer[][] board){

    }

    @Override
    public void onFragmentInteraction(Integer[][] puzzle, int fragment) {
        SolvedFragment solvedFragment = (SolvedFragment)fragmentPagerAdapter.getItem(SOLVED_FRAGMENT);
        BoardFragment boardFragment = (BoardFragment)fragmentPagerAdapter.getItem(BOARD_FRAGMENT);
        if (solvedFragment != null && fragment == BOARD_FRAGMENT) {
            solvedFragment.updatePuzzle(puzzle);
        }
        if(boardFragment != null && fragment == USER_FRAGMENT){
            boardFragment.updatePuzzle(puzzle);
        }
    }
}
