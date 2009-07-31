package org.ant4eclipse.pde.internal.tools;

import org.ant4eclipse.pde.model.featureproject.FeatureManifest;

/**
 * <p>
 * </p>
 *
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class FeatureDescription {

  /** - */
  private Object          _source;

  /** - */
  private FeatureManifest _featureManifest;

  /**
   * <p>
   * Creates a new instance of type FeatureDescription. 
   * </p>
   *
   * @param source the source of this feature (e.g. an eclipse feature project, a jar file or a directory))
   * @param featureManifest the {@link FeatureManifest}
   */
  public FeatureDescription(Object source, FeatureManifest featureManifest) {
    super();
    
    _source = source;
    _featureManifest = featureManifest;
  }

  /**
   * <p>
   * </p>
   *
   * @return the source
   */
  public Object getSource() {
    return _source;
  }

  /**
   * <p>
   * </p>
   *
   * @return the featureManifest
   */
  public FeatureManifest getFeatureManifest() {
    return _featureManifest;
  }
}