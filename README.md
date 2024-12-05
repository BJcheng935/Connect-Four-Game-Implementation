# Connect Four Game Implementation

A Java implementation of Connect Four featuring multiple AI algorithms and a tournament system.

## Components

- `ConnectFour.java`: Core game engine with board management and move validation
- `UCT.java`: Upper Confidence Bounds for Trees algorithm implementation
- `PMCGS.java`: Pure Monte Carlo Game Search implementation
- `Tournament.java`: Round-robin tournament system
- `Player.java`: Interactive player vs AI interface

## Features

- Board size: 6x7 (standard Connect Four dimensions)
- Multiple AI algorithms:
  - Uniform Random (UR)
  - Pure Monte Carlo Game Search (PMCGS)
  - Upper Confidence Bounds for Trees (UCT)
- Simulation options: 500 or 10000 iterations
- Output modes: Verbose, Brief, None

## Usage

### Running AI vs AI Tournament
```bash
java Tournament
```

### Playing Against AI
```bash
java Player
```
Select AI difficulty:
1. UCT (500 simulations)
2. UCT (10000 simulations)

### Running Single AI Game
```bash
java ConnectFour <input_file> <output_mode> <simulations>
```

## Input File Format
```
<algorithm>
<current_player>
<board_state>
```

## Output Modes
- `Verbose`: Detailed move analysis
- `Brief`: Final move values
- `None`: Only final move selected

## Author
- Bungein J Cheng