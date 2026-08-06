
# Implemented

1. Villagers score their homes
   - The scoring happens via a simple flood fill originating from the bed, where full blocks are considered solid as well as doors. Light levels are checked at each block.
   - In the scoring process, if beds that are claimed by other Villagers are found, the score will be evenly distributed between all bed owners.
   - The Scoring process only occurs when the Happiness value is needed, with an additional cooldown of <2 minutes.
<br><br>
2. Villagers will try to stay in the light. 
   - When Villagers wonder, they will always wonder on blocks near 
     a light source when they begin wandering already standing near a 
     light source, however, they wonder randomly when beginning wondering from a block with no light.
<br><br>
3. Villagers now have a "Happiness" value
   - This value corresponds to how satisfied a Villager is with its current living situation.
   - The happiness system works simmilar to the reputation system, with multiple types of happiness sources.
   - Currently, there are 5 types of happiness
     - ROOM: tied to how nice a Villager's room is.
     - TRADE: tied to trading
     - GIFT: tied to gifting
     - PAIN: negative value for when something bad happens to the villager.
   - Each happiness type has a max value
   - Any GIFT or TRADE happiness gained will first be used to increase the PAIN value towards a max of 0.
   - A Villagers happiness is treated as the sum of all types.
<br><br>
4. Hurting a Villager will decrease its happiness depending on how much damage was done.
<br><br>
5. Villagers can be given gifts
   - Giving gifts to villagers will increase their Happiness
   - Items that can be gifted to Villagers belong to the ``VILLAGER_GIFTABLE`` tag.
<br><br>
6. Only Level 4 and Level 5 of librarians offer Enchanted Book trades
    -  Villagers that have been gifted an enchanted book will sell that book when leveled up.
    - Villagers that have not been gifted a book will sell a random enchanted book, for level 4, it will be a random non-treasure enchanted book. For level 5, it will be a random Treasure enchanted book.
    - As of 26.1.2, this process is not data-driven due to lack of needed features.
      <br><br>
7. A Villager dying will greatly decrease the Happiness of all nearby Villagers, regardless of if they saw it or not.
   - Villagers that this concerns will emit particles.
     <br><br>
8. The Player can request that a Villager follow them.
   - The villager will only accept if it meets a minimum Happiness value and has a minimum reputation with the Player.
     <br><br>
9. Villagers will only level up if they meet extra requirements
    - All villager will need to meet minimum Happiness values before leveling up, as well as having a minimum home score.
      <br><br>
10. Villagers will only offer trades according to their Happiness
     - A fully leveled Villager will only offer trades for which the minimum requirements have been met.
       <br><br>
11. Librarians can be given a Signed Book where it will check the title or the first page for
    an enchantment, then sell that enchantment when it levels up, assuming a minimum happiness is met.
<br><br>
12. Villagers taking any damage will decrease their Happiness.
<br><br>
13. Librarians will only be able to sell enchantments of levels at or below their profession level.
<br><br>
14. Gifting a Villager an enchanted book of a higher level as one it sells will cause that Villager to instead sell the higher enchantment.
    <br><br>
15. Gifting a Villager an enchanted book of equal level to one it already sells will increase the level of said enchantment sold by 1 level.
<br><br>
16. Villagers will need to meet happiness requirements before breeding
<br><br>
17. Gifting a Villager food will add it to its inventory to be used during breeding.
<br><br>
18. Villagers will close all doors connected to their room during nighttime.


# Not Implemented

1. Villagers will sometimes try to wander outside and look at the sky. 
   - If they cannot see the sky, a PAIN happiness will be obtained.
2. Villagers will give discounts on goods if they reach a certain Happiness.
3. The Villager gui should display some indication of Happiness.

# Planned for the future
1. The scoring algorithm will be changed from a naive flood fill to a more advanced algorithm.
    - This new algorithm will determine how many blocks in the room a Villager could
actually pathfind to, in addition to checking the safety of the space.
<br><br>
2. Extra communal spaces shall be added in addition to the bell.
    - Pubs / Restaurants
    - Libraries
    - Interactable Wells
    - Soap Boxes (some block for strange Villagers to preach on)
    - These spaces will not come generated with the Village, it will be
up to the Player to create them.
<br><br>
3. There will be a new lightsource added called the Villager Light
    - this light can be gifted to villagers. Villagers can carry this light 
around. 
    - Villager Lights that are placed will eventually burn out unless interacted with. 
    - Villager's will interact with these lights, looking at them, and occasionally re-lighting them.
<br><br>
4. Traits will be added to Villagers, such as courage, weirdness.
    - Courage can be increased by gifting Villagers weapons
<br><br>
5. Guard Villagers will be implemented
    - Villagers who have enough courage will become guards.
    - Guards will patrol the village during work and idle hours.
    - Guards will re-light Villager Lights when needed.
<br><br>
6. Villager festivals will be added.
    - Villagers will occasionally partake in events such as festivals, given that certain requirements have been met.
<br><br>
7. Villager Funerals (Maybe, idk about this one)
    - Villagers will hold small funeral ceremonies for fallen Villagers.
<br><br>
8. A type of 'weird' Villager will be added.
    - This villager sells strange stuff, perhaps curses
<br><br>
9. A Bard Villager will be added.
    - A normal villager can be converted into a bard by gifting them instruments.
<br><br>
10. Villagers can be gifted room decorations.
    - Decorations will be things such as: pots, paintings, rugs.
    - Villagers who have room decorations on them will try to place them in the appropriate spots.
    - Successfully placed decorations will award extra points to their room score.
<br><br>
11. A Village post board could be added
    - This board will act almost like a sign, Villagers will come to it to try and plan festivals
    - The board will display missing requirements for festivals that Villagers are trying to produce.