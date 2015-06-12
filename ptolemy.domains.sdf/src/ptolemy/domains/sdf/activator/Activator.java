/* This activator registers an actor provider for the MyConst custom actor implementation.

Copyright (c) 2015 The Regents of the University of California; iSencia Belgium NV.
All rights reserved.

Permission is hereby granted, without written agreement and without
license or royalty fees, to use, copy, modify, and distribute this
software and its documentation for any purpose, provided that the above
copyright notice and the following two paragraphs appear in all copies
of this software.

IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA LIABLE TO ANY PARTY
FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES
ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
SUCH DAMAGE.

THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
ENHANCEMENTS, OR MODIFICATIONS.

PT_COPYRIGHT_VERSION_2
COPYRIGHTENDKEY
*/
package ptolemy.domains.sdf.activator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;
import org.ptolemy.commons.ThreeDigitVersionSpecification;
import org.ptolemy.commons.VersionSpecification;
import org.ptolemy.osgi.DefaultModelElementClassProvider;
import org.ptolemy.osgi.ModelElementClassProvider;

import ptolemy.domains.sdf.kernel.SDFDirector;
import ptolemy.domains.sdf.kernel.SDFIOPort;
import ptolemy.domains.sdf.kernel.SDFScheduler;

/**
 * This activator registers an actor provider for the MyConst custom actor implementation.
 *
 * @author erwinDL
 * @version $Id$
 * @since Ptolemy II 10.1
 * @Pt.ProposedRating Yellow (erwinDL)
 * @Pt.AcceptedRating Red (reviewmoderator)
 */
public class Activator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
	  
	  // FIXME figure out a more compact way to create a version-aware provider,
	  // that uses the bundle version but is not too dependent on OSGi APIs itself.
	  Version bundleVersion = context.getBundle().getVersion();
	  VersionSpecification providerVersion = new ThreeDigitVersionSpecification(
	      bundleVersion.getMajor(), 
	      bundleVersion.getMinor(), 
	      bundleVersion.getMicro(), 
	      bundleVersion.getQualifier());
	  
    _apSvcReg = context.registerService(ModelElementClassProvider.class.getName(), 
        new DefaultModelElementClassProvider(providerVersion, 
            SDFDirector.class,
            SDFScheduler.class
            ),
        null);
	}

	public void stop(BundleContext context) throws Exception {
	  _apSvcReg.unregister();
	}

	// private stuff
  /** The svc registration for the actor provider */
	private ServiceRegistration<?> _apSvcReg;
}
