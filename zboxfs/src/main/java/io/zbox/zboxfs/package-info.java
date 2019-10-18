/**
 * <p><a href="https://zbox.io/fs">ZboxFS</a> is a zero-details, privacy-focused in-app file system.
 * </p>
 *
 * <p>Its goal is to keep your app files securely, privately and reliably on underlying storage. By
 * encapsulating files and directories into an encrypted repository, it provides a virtual file
 * system and exclusive access to the authorised application.</p>
 *
 * <p>The most core parts of this module are {@link io.zbox.zboxfs.Repo} and {@link io.zbox.zboxfs.File},
 * which provides most API for file system operations and file data I/O.</p>
 *
 * <ul>
 * <li><b>{@link io.zbox.zboxfs.Repo}</b> provides file system manipulation methods, such as openFile,
 * createDir and etc.</li>
 * <li><b>{@link io.zbox.zboxfs.File}</b> provides file I/O methods, such as read, write, seek and etc.
 * </li>
 * </ul>
 *
 * <p>{@link io.zbox.zboxfs.Env#init(java.lang.String)} initialises the environment and should be called
 * once before any other methods provided by ZboxFS.</p>
 *
 * <p>After repository is opened by {@link io.zbox.zboxfs.RepoOpener}, all of the other functions provided
 * by ZboxFS will be thread-safe.</p>
 *
 * @author Bo Lu
 */
package io.zbox.zboxfs;