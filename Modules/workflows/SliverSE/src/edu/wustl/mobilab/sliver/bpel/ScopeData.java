/* Sliver - a BPEL execution engine for lightweight and mobile devices.
 * Copyright (C) 2006, Washington University in Saint Louis
 * By Gregory Hackmann.
 *
 * Washington University states that Sliver is free software;
 * you can redistribute it and/or modify it under the terms of
 * the current version of the GNU Lesser General Public License
 * as published by the Free Software Foundation.
 *
 * Sliver is distributed in the hope that it will be useful, but
 * THERE ARE NO WARRANTIES, WHETHER ORAL OR WRITTEN, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO, IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR USE.
 *
 * YOU UNDERSTAND THAT SLIVER IS PROVIDED "AS IS" FOR WHICH NO
 * WARRANTIES AS TO CAPABILITIES OR ACCURACY ARE MADE. THERE ARE NO
 * WARRANTIES AND NO REPRESENTATION THAT SLIVER IS FREE OF
 * INFRINGEMENT OF THIRD PARTY PATENT, COPYRIGHT, OR OTHER
 * PROPRIETARY RIGHTS.  THERE ARE NO WARRANTIES THAT SOFTWARE IS
 * FREE FROM "BUGS", "VIRUSES", "TROJAN HORSES", "TRAP DOORS", "WORMS",
 * OR OTHER HARMFUL CODE.
 *
 * YOU ASSUME THE ENTIRE RISK AS TO THE PERFORMANCE OF SOFTWARE AND/OR
 * ASSOCIATED MATERIALS, AND TO THE PERFORMANCE AND VALIDITY OF
 * INFORMATION GENERATED USING SOFTWARE. By using Sliver you agree to
 * indemnify, defend, and hold harmless WU, its employees, officers and
 * agents from any and all claims, costs, or liabilities, including
 * attorneys fees and court costs at both the trial and appellate levels
 * for any loss, damage, or injury caused by your actions or actions of
 * your officers, servants, agents or third parties acting on behalf or
 * under authorization from you, as a result of using Sliver.
 *
 * See the GNU Lesser General Public License for more details, which can
 * be found here: http://www.gnu.org/copyleft/lesser.html
 */

package edu.wustl.mobilab.sliver.bpel;

/**
 * Represents a BPEL process scope.
 * 
 * @author Greg Hackmann
 */
public class ScopeData
{
	/**
	 * The current scope's name.
	 */
	private String name;
	/**
	 * The {@link Partners} in the current scope.
	 */
	private Partners partners;
	/**
	 * The {@link Links} in the current scope.
	 */
	private Links links;
	/**
	 * The {@link Variables} in the current scope.
	 */
	private Variables variables;
	/**
	 * The {@link CorrelationSets} in the current scope.
	 */
	private CorrelationSets correlationSets;
	/**
	 * The {@link PartnerLinks} in the current scope.
	 */
	private PartnerLinks partnerLinks;
	/**
	 * Whether or not join failures should be ignored by default.
	 */
	private boolean suppressJoinFailure = false;
	/**
	 * Whether or not activities within this scope can refer to links in the
	 * parent scopes.
	 */
	private boolean linksCrossScopes = true;
	/**
	 * The list of nested scopes accessible to this scope.  The list is ordered
	 * such that the innermost scope is first in the list.  Note that the head
	 * of the list is the current scope, i.e., <tt>nestedScopes[0] == this</tt>.
	 */
	private ScopeData [] nestedScopes;
		
	/**
	 * Creates a new ScopeData.
	 * 
	 * @param name the new scope's name
	 * @param partnerLinks the {@link PartnerLinks} in the new scope
	 * @param partners the {@link Partners} in the new scope
	 * @param variables the {@link Variables} in the new scope
	 * @param correlationSets the {@link CorrelationSets} in the new scope
	 * @param suppressJoinFailure whether or not activities in the new scope
	 * should ignore link join failures
	 */
	ScopeData(String name, PartnerLinks partnerLinks, Partners partners,
		Variables variables, CorrelationSets correlationSets,
		boolean suppressJoinFailure)
	{
		this.name = name;
		this.partnerLinks = partnerLinks;
		this.partners = partners;
		this.variables = variables;
		this.correlationSets = correlationSets;
		this.suppressJoinFailure = suppressJoinFailure;

		links = null;
		nestedScopes = new ScopeData [] { this };
	}
	
	/**
	 * Creates a new ScopeData which represents a new scope nested within the
	 * current scope.  Activities within the nested scope can refer to links in
	 * its parent scopes; if this is not desirable (i.e., the new scope is
	 * an implicit scope created by a <tt>&lt;while&gt;</tt> activity), then
	 * use {@link #createNestedScope(boolean)}.
	 * 
	 * @return the new nested scope
	 */
	ScopeData createNestedScope()
	{
		return createNestedScope(true);
	}
	
	/**
	 * Creates a new ScopeData which represents a new scope nested within the
	 * current scope.
	 * 
	 * @param linksCrossScopes whether or not activities within the nested scope
	 * can refer to links in its parent scopes
	 * @return the new nested scope
	 */
	ScopeData createNestedScope(boolean linksCrossScopes)
	{
		ScopeData child = new ScopeData(null, partnerLinks, partners, null,
			null, suppressJoinFailure);
		// Create a new scope that duplicates this scope's partner links,
		// partners, and suppressJoinFailure attribute

		child.linksCrossScopes = linksCrossScopes;
		// Set the link-scope-crossing property

		child.nestedScopes = new ScopeData[nestedScopes.length + 1];
		child.nestedScopes[0] = child;
		System.arraycopy(nestedScopes, 0, child.nestedScopes, 1,
			nestedScopes.length);
		// Copy the scope nesting data, and add the new scope to the head
		
		return child;
	}
	
	/**
	 * Sets the scope's name.
	 * 
	 * @param name the scope's name
	 */
	void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Sets the scope's links.
	 * 
	 * @param links the scope's new {@link Links}
	 */
	void setLinks(Links links)
	{
		this.links = links;
	}

	/**
	 * Gets an inter-activity link.
	 * 
	 * @param linkName the link's name
	 * @return the {@link LinkSpecification} that represents the corresponding
	 * link, or <tt>null</tt> if there is no such link in this scope
	 */
	LinkSpecification getLink(String linkName)
	{
		int length = linksCrossScopes ? nestedScopes.length : 1;
		// If we aren't allowed to consult parent scopes, then limit our
		// search to the first nested scope (i.e., this)
		
		// For each nested scope
		for(int i = 0; i < length; i++)
		{
			if(nestedScopes[i].links != null
				&& nestedScopes[i].links.containsKey(linkName))
				return nestedScopes[i].links.getLinkSpecification(linkName);
			// Look for the link
		}
		
		return null;
		// If it's not there, then return null
	}
	
	/**
	 * Sets the scope's variables.
	 * 
	 * @param variables the scope's new {@link Variables}
	 */
	void setVariables(Variables variables)
	{
		this.variables = variables;
	}
	
	/**
	 * Gets a variable.
	 * 
	 * @param variableName the variables's name
	 * @return the {@link VariableSpecification} that represents the
	 * corresponding variable, or <tt>null</tt> if there is no such variable
	 * in this scope
	 */
	public VariableSpecification getVariable(String variableName)
	{
		// For each nested scope
		for(int i = 0; i < nestedScopes.length; i++)
		{
			if(nestedScopes[i].variables != null
				&& nestedScopes[i].variables.containsKey(variableName))
				return nestedScopes[i].variables.getVariableSpecification(variableName);
			// Look for the variable
		}
		
		return null;
		// If it's not there, then return null
	}

	/**
	 * Adds correlation sets to the current scope.
	 * 
	 * @param correlationSets the new {@link CorrelationSets} to add to the
	 * scope
	 */
	void setCorrelationSets(CorrelationSets correlationSets)
	{
		this.correlationSets = correlationSets;
	}
	
	/**
	 * Gets a partner link.
	 * 
	 * @param partnerLinkName the partner links's name
	 * @return the {@link PartnerLink} that represents the corresponding partner
	 * link, or <tt>null</tt> if there is no such partner link in this scope
	 */
	public PartnerLink getPartnerLink(String partnerLinkName)
	{
		return partnerLinks.getPartnerLink(partnerLinkName);
	}

	/**
	 * Gets a partner group.
	 * 
	 * @param partnerGroupName the partner links's name
	 * @return the {@link PartnerGroup} that represents the corresponding partner
	 * group, or <tt>null</tt> if there is no such partner group in this scope
	 */
	public PartnerGroup getPartnerGroup(String partnerGroupName)
	{
		return partnerLinks.getPartnerGroup(partnerGroupName);
	}
	
	/**
	 * Sets whether or not join failures should be ignored by default.
	 * 
	 * @param suppressJoinFailure whether or not join failures should be ignored
	 * by default
	 */
	void setSuppressJoinFailure(boolean suppressJoinFailure)
	{
		this.suppressJoinFailure = suppressJoinFailure;
	}
	
	/**
	 * Gets whether or not join failures should be ignored by default.
	 * 
	 * @return whether or not join failures should be ignored by default
	 */
	boolean isSuppressJoinFailure()
	{
		return suppressJoinFailure;
	}
}
