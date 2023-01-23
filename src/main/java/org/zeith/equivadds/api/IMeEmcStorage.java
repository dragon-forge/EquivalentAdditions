package org.zeith.equivadds.api;

import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage;

/**
 * Interface for instances of {@link moze_intel.projecte.api.capabilities.block_entity.IEmcStorage},
 * but exposes EMC (Energy-Matter Currency) for external Matter Energy (ME) storage.
 * This interface is typically used for ME Storage Bus.
 * <p>
 * Implementations of this interface are expected to provide methods for accessing and manipulating
 * the external EMC stored in the ME storage.
 *
 * @author zeith
 */
public interface IMeEmcStorage
		extends IEmcStorage
{
	/**
	 * Get the current amount of EMC stored, which is visible to the external ME storage.
	 *
	 * @return the current EMC stored here.
	 */
	long getExternalMeEmc();
	
	/**
	 * Get the maximum EMC capacity, visible to the ME Storage Bus.
	 *
	 * @return the maximum EMC capacity, visible to the ME Storage Bus.
	 */
	long getExternalEmcCapacity();
	
	/**
	 * Inserts EMC through the external ME storage
	 *
	 * @param amount
	 * 		the amount of EMC to be inserted
	 * @param action
	 * 		should the action be simulated, or actually performed.
	 *
	 * @return the amount of EMC that was actually inserted.
	 */
	long insertExternalEmc(long amount, EmcAction action);
	
	/**
	 * Extracts EMC through the external ME storage.
	 *
	 * @param amount
	 * 		the amount of EMC to be extracted
	 * @param action
	 * 		should the action be simulated, or actually performed.
	 *
	 * @return the amount of EMC that was actually extracted.
	 */
	long extractExternalEmc(long amount, EmcAction action);
}