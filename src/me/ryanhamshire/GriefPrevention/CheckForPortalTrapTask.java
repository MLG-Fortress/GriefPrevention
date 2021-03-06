/*
    GriefPrevention Server Plugin for Minecraft
    Copyright (C) 2012 Ryan Hamshire

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
 
package me.ryanhamshire.GriefPrevention;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

//players can be "trapped" in a portal frame if they don't have permission to break
//solid blocks blocking them from exiting the frame
//if that happens, we detect the problem and send them back through the portal.
class CheckForPortalTrapTask implements Runnable 
{
	//player who recently teleported via nether portal 
	private Player player;
	
	//where to send the player back to if he hasn't left the portal frame
	private Location returnLocation;
	
	public CheckForPortalTrapTask(Player player, Location location)
	{
		this.player = player;
		this.returnLocation = location;
	}
	
	@Override
	public void run()
	{
	    //if player has logged out, do nothing
	    if(!player.isOnline()) return;

		Block playerBlock = this.player.getLocation().getBlock();
	    //if still standing in a portal frame, teleport him back through
	    if(playerBlock.getType() == Material.PORTAL || isInNonOccludingBlock(playerBlock))
	    {
	        this.player.teleport(this.returnLocation);
	    }
	    
	    //otherwise, note that he 'escaped' the portal frame
	    else
	    {
	        PlayerEventHandler.portalReturnMap.remove(player.getUniqueId());
	    }
	}

	boolean isInNonOccludingBlock(Block block)
	{
		Material playerBlock = block.getType();
		//Most blocks you can "stand" inside but cannot pass (isSolid) usually can be seen through (!isOccluding)
		if ((!playerBlock.isSolid() || playerBlock.isOccluding()))
		{
			//Check the block above
			playerBlock = block.getRelative(BlockFace.UP).getType();
			if ((!playerBlock.isSolid() || playerBlock.isOccluding()))
				return false;
		}
		if (block.getRelative(BlockFace.EAST).getType() == Material.PORTAL
				|| block.getRelative(BlockFace.WEST).getType() == Material.PORTAL
				|| block.getRelative(BlockFace.NORTH).getType() == Material.PORTAL
				|| block.getRelative(BlockFace.SOUTH).getType() == Material.PORTAL)
			return true;
		return false;
	}
}
