**README.md**

# Networked Othello

Play a classic game of Othello (Reversi) over a local network against another human player.

## Features

* Two-player gameplay over a local network
* Standard Othello rules 
* Clear board display 

## Installation

**Prerequisites**

* [Java]

**Instructions**
1. Clone the repository:
   ```bash
   git clone https://github.com/NamHaiBui/Othello.git
   ```
2. Navigate to the project directory:
   ```bash
   cd Othello
   ```

## Usage

1. **Start Player 1:**
   * Run the Othello file.
   * Select "HumanP2PPlayer" as the player type.
2. **Start Player 2:**
   * Repeat the steps for Player 1 on a separate device or instance of the game.
3. **Connection:**
   * When prompted, both players should enter:
      * IP Address: localhost (or your IP address for LAN)
      * Port: 1111

## Gameplay

* Players take turns placing their colored pieces (black or white) on the board.
* Pieces must be placed to outflank and 'flip' an opponent's piece(s).
* The game ends when the board is full or neither player can make a valid move.
* The player with the most pieces of their color wins.

## Contributing

We welcome contributions to the Othello project!

1. Fork the repository.
2. Create a new branch.
3. Make your changes.
4. Submit a pull request.

## License

This project is licensed under the MIT License: [https://opensource.org/licenses/MIT](https://opensource.org/licenses/MIT) - see the LICENSE file for details.
