package org.wgpu4j.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wgpu4j.bindings.*;

import java.lang.foreign.*;

/**
 * Helper class for creating Linux X11 surface sources.
 */
public class LinuxSurfaceHelper {
    private static final Logger logger = LoggerFactory.getLogger(LinuxSurfaceHelper.class);

    /**
     * Creates an X11 surface source for Linux platforms.
     *
     * @param arena      The memory arena to allocate in
     * @param x11Window  The X11 window handle
     * @param x11Display The X11 display handle
     * @return MemorySegment representing the surface source
     */
    public static MemorySegment createX11SurfaceSource(Arena arena, long x11Window, long x11Display) {
        validateHandle(x11Window, "X11 Window");
        validateHandle(x11Display, "X11 Display");

        MemorySegment surfaceSource = WGPUSurfaceSourceXlibWindow.allocate(arena);
        MemorySegment chain = WGPUSurfaceSourceXlibWindow.chain(surfaceSource);
        WGPUChainedStruct.next(chain, MemorySegment.NULL);
        WGPUChainedStruct.sType(chain, webgpu_h.WGPUSType_SurfaceSourceXlibWindow());
        WGPUSurfaceSourceXlibWindow.display(surfaceSource, MemorySegment.ofAddress(x11Display));
        WGPUSurfaceSourceXlibWindow.window(surfaceSource, (int) x11Window);

        logger.info("Created X11 surface source with window: 0x{}, display: 0x{}",
                Long.toHexString(x11Window), Long.toHexString(x11Display));

        return surfaceSource;
    }

    /**
     * Creates a Wayland surface source for Linux platforms.
     *
     * @param arena          The memory arena to allocate in
     * @param waylandWindow  The Wayland window handle
     * @param waylandDisplay The Wayland display handle
     * @return MemorySegment representing the surface source
     */
    public static MemorySegment createWaylandSurfaceSource(Arena arena, long waylandWindow, long waylandDisplay) {
        validateHandle(waylandWindow, "Wayland Window");
        validateHandle(waylandDisplay, "Wayland Display");
        MemorySegment surfaceSource = WGPUSurfaceSourceWaylandSurface.allocate(arena);
        MemorySegment chain = WGPUSurfaceSourceWaylandSurface.chain(surfaceSource);
        WGPUChainedStruct.next(chain, MemorySegment.NULL);
        WGPUChainedStruct.sType(chain, webgpu_h.WGPUSType_SurfaceSourceWaylandSurface());
        WGPUSurfaceSourceWaylandSurface.display(surfaceSource, MemorySegment.ofAddress(waylandDisplay));
        WGPUSurfaceSourceWaylandSurface.surface(surfaceSource, MemorySegment.ofAddress(waylandWindow));

        logger.info("Created Wayland surface source with window: 0x{}, display: 0x{}",
                Long.toHexString(waylandWindow), Long.toHexString(waylandDisplay));

        return surfaceSource;
    }

    /**
     * Validates that a native handle is not null/zero.
     *
     * @param handle     The handle to validate
     * @param handleType The type of handle for error messages
     * @throws IllegalArgumentException if the handle is invalid
     */
    public static void validateHandle(long handle, String handleType) {
        if (!isValidHandle(handle)) {
            throw new IllegalArgumentException(handleType + " handle cannot be null/zero");
        }
    }

    /**
     * Evaluates whether a native handle is not null/zero.
     *
     * @param handle The handle to validate
     * @return bool representing handle validity
     */
    public static boolean isValidHandle(long handle) {
        return handle != 0;
    }
}