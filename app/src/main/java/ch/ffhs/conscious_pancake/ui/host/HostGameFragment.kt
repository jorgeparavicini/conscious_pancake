package ch.ffhs.conscious_pancake.ui.host

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ch.ffhs.conscious_pancake.databinding.FragmentHostGameBinding
import ch.ffhs.conscious_pancake.vo.enums.GameSize
import ch.ffhs.conscious_pancake.vo.enums.PartyType
import ch.ffhs.conscious_pancake.vo.enums.TurnDuration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HostGameFragment : Fragment() {

    private var _binding: FragmentHostGameBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HostGameViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHostGameBinding.inflate(inflater, container, false)
        val partyTypeAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, PartyType.values()
        )
        val gameSizeAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, GameSize.values()
        )
        val turnDurationAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, TurnDuration.values()
        )

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }

        binding.apply {
            partyTypeSpinner.adapter = partyTypeAdapter
            partyTypeSpinner.setSelection(viewModel.lobby.partyType.index)
            partyTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    viewModel.lobby.partyType =
                        PartyType.fromIndex(position) ?: PartyType.fromIndex(0)!!
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    viewModel.lobby.partyType = PartyType.fromIndex(0)!!
                    partyTypeSpinner.setSelection(0)
                }
            }

            gameSizeSpinner.adapter = gameSizeAdapter
            gameSizeSpinner.setSelection(viewModel.lobby.gameSize.index)
            gameSizeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    viewModel.lobby.gameSize =
                        GameSize.fromIndex(position) ?: GameSize.fromIndex(0)!!
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    viewModel.lobby.gameSize = GameSize.fromIndex(0)!!
                    gameSizeSpinner.setSelection(0)
                }

            }

            turnDurationSpinner.adapter = turnDurationAdapter
            turnDurationSpinner.setSelection(viewModel.lobby.turnDuration.index)
            turnDurationSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?, view: View?, position: Int, id: Long
                    ) {
                        viewModel.lobby.turnDuration =
                            TurnDuration.fromIndex(position) ?: TurnDuration.fromIndex(0)!!
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        viewModel.lobby.turnDuration = TurnDuration.fromIndex(0)!!
                        turnDurationSpinner.setSelection(0)
                    }

                }

            hostGameStart.setOnClickListener {
                viewModel.hostGame {
                }
            }
        }
        return binding.root
    }
}