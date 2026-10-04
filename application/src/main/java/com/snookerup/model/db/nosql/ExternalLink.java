package com.snookerup.model.db.nosql;

import lombok.Data;

/**
 * Models an external link on a routine, providing more context for a routine, such as a writeup on an external site or
 * a YouTube video.
 *
 * @author Huw
 */
@Data
public class ExternalLink {

    /** A human-readable label, e.g. "Shaun Murphy Snooker - The Line Up". */
    private String label;

    /** The actual URL to link to. */
    private String url;

    /** The type of the external link. Used to style the link in the UI. */
    private ExternalLinkType type;
}