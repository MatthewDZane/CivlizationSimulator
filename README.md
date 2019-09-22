# CivlizationSimulator
Simulates the growth of civilizations as they compete for territory over the map.

User sets the size of the square map and the number of civiliations. The program will generate a random map filled with desert and fertile
tiles. Then a location for the each civilizations capital will be chosen. Then the civilization will begin.

Civilization Behavior:
Civilizations will 50% of the time either attack a neighboring civilization or expand into a neighboring unoccupied territory. If there are
no neighboring civiliations then it will expand into unoccupied territory. They will target tiles closer to their own capital, while giving
some favorability to fertile tiles. When one attacks another, the civilization with the greater strength will win owner ship of the tile.
Strength is determined by size of the civilization minus the distance of the target tile from their respective capital. The defender gets a
10% bonus also. When the capital is captured the defending civiliation will relocate their capital to a random place in their territory.
A civiliation will be defeated when all of its territory is taken over.

How to run:
  Type the executable name in the command prompt followed by two arguments, an int representing the length of the square map, so the number
  of tiles will be the value to the sencond power, and an int representing the number of civilizations to start out with.
  Ex: Java -jar CiviliationSimulator.jar 100 10
