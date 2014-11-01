/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jooby;

import static java.util.Objects.requireNonNull;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Give you access at the current HTTP request in order to read parameters, headers and body.
 *
 * @author edgar
 * @since 0.1.0
 */
public interface Request {

  /**
   * Forwarding request.
   *
   * @author edgar
   * @since 0.1.0
   */
  class Forwarding implements Request {

    /** Target request. */
    private Request request;

    /**
     * Creates a new {@link Forwarding} request.
     *
     * @param request A target request.
     */
    public Forwarding(final @Nonnull Request request) {
      this.request = requireNonNull(request, "A HTTP request is required.");
    }

    @Override
    public String path() {
      return request.path();
    }

    @Override
    public Verb verb() {
      return request.verb();
    }

    @Override
    public MediaType type() {
      return request.type();
    }

    @Override
    public List<MediaType> accept() {
      return request.accept();
    }

    @Override
    public Optional<MediaType> accepts(final List<MediaType> types) {
      return request.accepts(types);
    }

    @Override
    public Map<String, Mutant> params() throws Exception {
      return request.params();
    }

    @Override
    public Mutant param(final String name) throws Exception {
      return request.param(name);
    }

    @Override
    public Mutant header(final String name) {
      return request.header(name);
    }

    @Override
    public Map<String, Mutant> headers() {
      return request.headers();
    }

    @Override
    public Optional<Cookie> cookie(final String name) {
      return request.cookie(name);
    }

    @Override
    public List<Cookie> cookies() {
      return request.cookies();
    }

    @Override
    public <T> T body(final TypeLiteral<T> type) throws Exception {
      return request.body(type);
    }

    @Override
    public <T> T getInstance(final Key<T> key) {
      return request.getInstance(key);
    }

    @Override
    public Charset charset() {
      return request.charset();
    }

    @Override
    public long length() {
      return request.length();
    }

    @Override
    public Locale locale() {
      return request.locale();
    }

    @Override
    public String ip() {
      return request.ip();
    }

    @Override
    public Route route() {
      return request.route();
    }

    @Override
    public Session session() {
      return request.session();
    }

    @Override
    public Optional<Session> ifSession() {
      return request.ifSession();
    }

    @Override
    public String hostname() {
      return request.hostname();
    }

    @Override
    public String protocol() {
      return request.protocol();
    }

    @Override
    public Optional<MediaType> accepts(final MediaType... types) {
      return request.accepts(types);
    }

    @Override
    public Optional<MediaType> accepts(final String... types) {
      return request.accepts(types);
    }

    @Override
    public <T> T body(final Class<T> type) throws Exception {
      return request.body(type);
    }

    @Override
    public <T> T getInstance(final Class<T> type) {
      return request.getInstance(type);
    }

    @Override
    public <T> T getInstance(final TypeLiteral<T> type) {
      return request.getInstance(type);
    }

    @Override
    public boolean secure() {
      return request.secure();
    }

    @Override
    public boolean xhr() {
      return request.xhr();
    }

    @Override
    public String toString() {
      return request.toString();
    }

    /**
     * Unwrap a request in order to find out the target instance.
     *
     * @param req A request.
     * @return A target instance (not a {@link Forwarding}).
     */
    public static Request unwrap(final @Nonnull Request req) {
      requireNonNull(req, "A request is required.");
      Request root = req;
      while (root instanceof Forwarding) {
        root = ((Forwarding) root).request;
      }
      return root;
    }
  }

  /**
   * Jooby doesn't use a custom scope annotation for request scoped object. Request scoped object
   * are binded using a child injector per each request.
   *
   * <h1>Providing request scoped objects</h1>
   * <p>
   * Jooby give you an extension point in order to register scope requested objects, here is how do
   * you usually do it.
   * </p>
   *
   * <pre>
   * class MyModule implements Jooby.Module {
   *   void configure(Mode mode, Config config, Binder binder) {
   *     Multibinder b = Multibinder.newSetBinder(binder, RequestModule.class);
   *     b.addBinding().toInstance(requestBinder -> {
   *       b.bind(MyService.class).to(...);
   *     })
   *   }
   * }
   * </pre>
   *
   * <h1>Do I have to provide request objects?</h1>
   * <p>
   * You don't. Request scoped object are useful if you need/want to have a single instance of an
   * object per request. A good example of such object is a db session, bc you want to reuse the
   * session during the request execution.
   * </p>
   * <p>
   * If you don't need/have that requirement. You shouldn't use request scoped object and just work
   * with prototype objects, as Guice suggest.
   * </p>
   *
   * @author edgar
   * @since 0.1.0
   */
  interface Module {

    void configure(Binder binder);

  }

  /**
   * Given:
   *
   * <pre>
   *  http://domain.com/some/path.html -> /some/path.html
   *  http://domain.com/a.html         -> /a.html
   * </pre>
   *
   * @return The request URL pathname.
   */
  @Nonnull
  default String path() {
    return route().path();
  }

  /**
   * @return Current request verb.
   */
  default Verb verb() {
    return route().verb();
  }

  /**
   * @return The value of the <code>Content-Type</code> header. Default is: {@literal*}/{@literal*}.
   */
  @Nonnull
  MediaType type();

  /**
   * @return The value of the <code>Accept header</code>. Default is: {@literal*}/{@literal*}.
   */
  @Nonnull
  List<MediaType> accept();

  /**
   * Check if the given types are acceptable, returning the best match when true, or else
   * Optional.empty.
   *
   * <pre>
   * // Accept: text/html
   * req.accepts("text/html");
   * // => "text/html"
   *
   * // Accept: text/*, application/json
   * req.accepts("text/html");
   * // => "text/html"
   * req.accepts("text/html");
   * // => "text/html"
   * req.accepts("application/json" "text/plain");
   * // => "application/json"
   * req.accepts("application/json");
   * // => "application/json"
   *
   * // Accept: text/*, application/json
   * req.accepts("image/png");
   * // => Optional.empty
   *
   * // Accept: text/*;q=.5, application/json
   * req.accepts("text/html", "application/json");
   * // => "application/json"
   * </pre>
   *
   * @param types
   * @return The best acceptable type.
   */
  default @Nonnull Optional<MediaType> accepts(@Nonnull final String... types) {
    return accepts(MediaType.valueOf(types));
  }

  /**
   * Check if the given types are acceptable, returning the best match when true, or else
   * Optional.empty.
   *
   * <pre>
   * // Accept: text/html
   * req.accepts("text/html");
   * // => "text/html"
   *
   * // Accept: text/*, application/json
   * req.accepts("text/html");
   * // => "text/html"
   * req.accepts("text/html");
   * // => "text/html"
   * req.accepts("application/json" "text/plain");
   * // => "application/json"
   * req.accepts("application/json");
   * // => "application/json"
   *
   * // Accept: text/*, application/json
   * req.accepts("image/png");
   * // => Optional.empty
   *
   * // Accept: text/*;q=.5, application/json
   * req.accepts("text/html", "application/json");
   * // => "application/json"
   * </pre>
   *
   * @param types
   * @return The best acceptable type.
   */
  default @Nonnull Optional<MediaType> accepts(@Nonnull final MediaType... types) {
    return accepts(ImmutableList.copyOf(types));
  }

  /**
   * Check if the given types are acceptable, returning the best match when true, or else
   * Optional.empty.
   *
   * <pre>
   * // Accept: text/html
   * req.accepts("text/html");
   * // => "text/html"
   *
   * // Accept: text/*, application/json
   * req.accepts("text/html");
   * // => "text/html"
   * req.accepts("text/html");
   * // => "text/html"
   * req.accepts("application/json" "text/plain");
   * // => "application/json"
   * req.accepts("application/json");
   * // => "application/json"
   *
   * // Accept: text/*, application/json
   * req.accepts("image/png");
   * // => Optional.empty
   *
   * // Accept: text/*;q=.5, application/json
   * req.accepts("text/html", "application/json");
   * // => "application/json"
   * </pre>
   *
   * @param types
   * @return The best acceptable type.
   */
  @Nonnull
  Optional<MediaType> accepts(@Nonnull List<MediaType> types);

  /**
   * Get all the available parameter. A HTTP parameter can be provided in any of
   * these forms:
   *
   * <ul>
   * <li>Path parameter, like: <code>/path/:name</code> or <code>/path/{name}</code></li>
   * <li>Query parameter, like: <code>?name=jooby</code></li>
   * <li>Body parameter when <code>Content-Type</code> is
   * <code>application/x-www-form-urlencoded</code> or <code>multipart/form-data</code></li>
   * </ul>
   *
   * @return All the parameters.
   */
  @Nonnull
  Map<String, Mutant> params() throws Exception;

  /**
   * Get a HTTP request parameter under the given name. A HTTP parameter can be provided in any of
   * these forms:
   * <ul>
   * <li>Path parameter, like: <code>/path/:name</code> or <code>/path/{name}</code></li>
   * <li>Query parameter, like: <code>?name=jooby</code></li>
   * <li>Body parameter when <code>Content-Type</code> is
   * <code>application/x-www-form-urlencoded</code> or <code>multipart/form-data</code></li>
   * </ul>
   *
   * The order of precedence is: <code>path</code>, <code>query</code> and <code>body</code>. For
   * example a pattern like: <code>GET /path/:name</code> for <code>/path/jooby?name=rocks</code>
   * produces:
   *
   * <pre>
   *  assertEquals("jooby", req.param(name).stringValue());
   *
   *  assertEquals("jooby", req.param(name).toList(String.class).get(0));
   *  assertEquals("rocks", req.param(name).toList(String.class).get(1));
   * </pre>
   *
   * Uploads can be retrieved too when <code>Content-Type</code> is <code>multipart/form-data</code>
   * see {@link Upload} for more information.
   *
   * @param name A parameter's name.
   * @return A HTTP request parameter.
   * @throws Exception On retrieval failures.
   * @see {@link Mutant}
   */
  @Nonnull
  Mutant param(@Nonnull String name) throws Exception;

  /**
   * Get a HTTP header.
   *
   * @param name A header's name.
   * @return A HTTP request header.
   * @see {@link Mutant}
   */
  @Nonnull
  Mutant header(@Nonnull String name);

  /**
   * @return All the headers.
   */
  @Nonnull
  Map<String, Mutant> headers();

  /**
   * Get a cookie with the given name (if present).
   *
   * @param name Cookie's name.
   * @return A cookie or an empty optional.
   */
  @Nonnull
  Optional<Cookie> cookie(@Nonnull String name);

  /**
   * @return All the cookies.
   */
  @Nonnull
  List<Cookie> cookies();

  /**
   * Convert the HTTP request body into the given type.
   *
   * @param type The body type.
   * @return The HTTP body as an object.
   * @throws Exception If body can't be converted or there is no HTTP body.
   * @see {@link BodyConverter#read(TypeLiteral, Body.Reader)}
   */
  @Nonnull
  default <T> T body(@Nonnull final Class<T> type) throws Exception {
    requireNonNull(type, "A body type is required.");
    return body(TypeLiteral.get(type));
  }

  /**
   * Convert the HTTP request body into the given type.
   *
   * @param type The body type.
   * @return The HTTP body as an object.
   * @throws Exception If body can't be converted or there is no HTTP body.
   * @see {@link BodyConverter#read(TypeLiteral, Body.Reader)}
   */
  @Nonnull
  <T> T body(@Nonnull TypeLiteral<T> type) throws Exception;

  /**
   * Creates a new instance (if need it) and inject required dependencies. Request scoped object
   * can registered using a {@link Request.Module}.
   *
   * @param type A body type.
   * @return A ready to use object.
   * @see Request.Module
   */
  @Nonnull
  default <T> T getInstance(@Nonnull final Class<T> type) {
    return getInstance(Key.get(type));
  }

  /**
   * Creates a new instance (if need it) and inject required dependencies. Request scoped object
   * can registered using a {@link Request.Module}.
   *
   * @param type A body type.
   * @return A ready to use object.
   * @see Request.Module
   */
  @Nonnull
  default <T> T getInstance(@Nonnull final TypeLiteral<T> type) {
    return getInstance(Key.get(type));
  }

  /**
   * Creates a new instance (if need it) and inject required dependencies. Request scoped object
   * can registered using a {@link Request.Module}.
   *
   * @param key A body key.
   * @return A ready to use object.
   * @see Request.Module
   */
  @Nonnull
  <T> T getInstance(@Nonnull Key<T> key);

  /**
   * The charset defined in the request body. If the request doesn't specify a character
   * encoding, this method return the global charset: <code>application.charset</code>.
   *
   * @return A current charset.
   */
  @Nonnull
  Charset charset();

  /**
   * Get the content of the <code>Accept-Language</code> header. If the request doens't specify
   * such header, this method return the global locale: <code>application.lang</code>.
   *
   * @return A locale.
   */
  @Nonnull
  Locale locale();

  /**
   * @return The length, in bytes, of the request body and made available by the input stream, or
   *         <code>-1</code> if the length is not known.
   */
  long length();

  /**
   * @return The IP address of the client or last proxy that sent the request.
   */
  @Nonnull
  String ip();

  /**
   * @return The currently matched {@link Route}.
   */
  @Nonnull
  Route route();

  /**
   * The fully qualified name of the client or the last proxy that sent the request.
   * If the engine cannot or chooses not to resolve the hostname (to improve performance),
   * this method returns the dotted-string form of the IP address
   *
   * @return The fully qualified name of the client or the last proxy that sent the request.
   */
  String hostname();

  /**
   * @return The current session associated with this request or if the request does not have a
   *         session, creates one.
   */
  Session session();

  /**
   * @return The current session associated with this request if there is one.
   */
  Optional<Session> ifSession();

  /**
   * @return True if the <code>X-Requested-With</code> header is set to <code>XMLHttpRequest</code>.
   */
  default boolean xhr() {
    return header("X-Requested-With")
        .toOptional(String.class)
        .map("XMLHttpRequest"::equalsIgnoreCase)
        .orElse(Boolean.FALSE);
  }

  /**
   * @return The name and version of the protocol the request uses in the form
   *         <i>protocol/majorVersion.minorVersion</i>, for example, HTTP/1.1
   */
  String protocol();

  /**
   * @return True if this request was made using a secure channel, such as HTTPS.
   */
  boolean secure();

}